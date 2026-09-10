package org.alveolo.ffm.processor;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.Symbol;

class ExecutableGenerator {
  final Messager messager;
  final Elements elements;
  final ExecutableElement element;
  final boolean hasErrors;
  final String methodHandleName;
  final boolean instanceMethodHandle;
  final List<NativeArgument> leadingNativeArguments;
  final String linkerExpression;
  final String lookupExpression;
  final TypeGenerator returnGenerator;
  final List<VariableGenerator> parameterGenerators;
  final ForeignMemoryAnalyzer memoryAnalyzer;
  private final List<VariableGenerator> allocatorParameters;
  private final List<VariableGenerator> callStateParameters;
  private final List<VariableGenerator> nativeParameters;
  private final AllocationPlan allocationPlan;

  record NativeArgument(String layout, String expression) {}
  record LocalAllocation(
      String name, String byteSize, String alignment) {}
  record AllocationPlan(boolean confinedArena,
      List<LocalAllocation> allocations, boolean shared) {
    AllocationPlan {
      allocations = List.copyOf(allocations);
    }

    LocalAllocation allocation(String name) {
      return allocations.stream()
          .filter(allocation -> allocation.name().equals(name))
          .findFirst().orElseThrow();
    }
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes,
      ExecutableElement element, String methodHandleName) {
    this(processingEnv, generatedTypes, element, methodHandleName, false);
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle) {
    this(processingEnv, generatedTypes, element, methodHandleName,
        instanceMethodHandle, List.of(), "Linker$F", "SymbolLookup$F");
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle,
      List<NativeArgument> leadingNativeArguments,
      String linkerExpression, String lookupExpression) {
    messager = processingEnv.getMessager();
    elements = processingEnv.getElementUtils();
    this.element = element;
    this.methodHandleName = methodHandleName;
    this.instanceMethodHandle = instanceMethodHandle;
    this.leadingNativeArguments = leadingNativeArguments;
    this.linkerExpression = linkerExpression;
    this.lookupExpression = lookupExpression;
    memoryAnalyzer = new ForeignMemoryAnalyzer(
        processingEnv, generatedTypes);

    returnGenerator = new TypeGenerator(
        processingEnv, generatedTypes, element.getReturnType(), element);

    parameterGenerators = element.getParameters().stream()
        .map(param -> new VariableGenerator(
            processingEnv, generatedTypes, param))
        .toList();

    allocatorParameters = parameterGenerators.stream()
        .filter(TypeGenerator::isSegmentAllocator)
        .toList();
    callStateParameters = parameterGenerators.stream()
        .filter(TypeGenerator::isCallState)
        .toList();
    nativeParameters = parameterGenerators.stream()
        .filter(not(TypeGenerator::isSegmentAllocator))
        .filter(not(TypeGenerator::isCallState))
        .toList();

    hasErrors = checkParameterTypes();
    allocationPlan = planAllocations();
  }

  String methodWithHandle() {
    if (hasErrors) return throwingMethodPlaceholder();

    return methodHandleDeclaration() + methodImpl(methodHandleName);
  }

  String methodOnly(String methodHandleExpression) {
    if (hasErrors) return throwingMethodPlaceholder();

    return methodImpl(methodHandleExpression);
  }

  private String throwingMethodPlaceholder() {
    return """

          <signature> {
            throw new RuntimeException("Check compile errors!");
          }
        """
        .replace("<signature>", signature());
  }

  private String methodImpl(String methodHandleExpression) {
    return """

          <signature> {
            <symbolGuard>
            <declarations>
            try <confinedArena>{
              <body>
            } catch (RuntimeException|Error exception$f) {
              throw exception$f;
            } catch (Throwable throwable$f) {
              throw new AssertionError(throwable$f);
            }<finallyBlock>
          }
        """
        .replace("<signature>", signature())
        .replace("    <symbolGuard>\n", symbolGuard())
        .replace("    <declarations>\n", declarations())
        .replace("<confinedArena>", confinedArena())
        .replace("<body>", methodBody(methodHandleExpression))
        .replace("<finallyBlock>", finallyBlock());
  }

  private String symbolGuard() {
    if (instanceMethodHandle) return "";

    return """
            if (<mh> == null)
              throw new UnsatisfiedLinkError(<message>);
        """
        .replace("<mh>", methodHandleName)
        .replace("<message>", elements.getConstantExpression(
            "Native symbol not found: " + name(element)));
  }

  String methodHandleDeclaration() {
    if (instanceMethodHandle)
      return """

            private final java.lang.invoke.MethodHandle <mh>;
          """
          .replace("<mh>", methodHandleName);

    var rawHandle = """
        <linker>.downcallHandle(
            address$f,
            <descriptor><options>)
        """
        .replace("<linker>", linkerExpression)
        .replace("<descriptor>", downcallDescriptor())
        .replace("<options>", downcallOptions())
        .stripTrailing();

    return """

          private static final java.lang.invoke.MethodHandle <mh> =
              <lookup>.find(<name>)
                  .map(address$f -> <initializer>)
                  .orElse(null);
        """
        .replace("<mh>", methodHandleName)
        .replace("<lookup>", lookupExpression)
        .replace("<name>", elements.getConstantExpression(name(element)))
        .replace("<initializer>", adaptDowncall(rawHandle, false)
            .replace("\n", "\n          "));
  }

  String adaptDowncall(String rawHandle, boolean unbound) {
    if (!needsDowncallAdaptation()) return rawHandle;

    var arguments = Stream.concat(
        Stream.ofNullable(unbound ? "null" : null),
        downcallArguments("null", _ -> "null", _ -> "null",
            parameter -> parameter.needsDowncallAdaptation()
                ? parameter.canonicalRuntimeType() : "null"))
        .toList();

    var suffix = arguments.isEmpty() ? "" : """
        ,
            new org.alveolo.ffm.NativeType[] {
                <arguments>
            }
        """
        .replace("<arguments>", String.join(",\n        ", arguments))
        .stripTrailing();

    return """
        org.alveolo.ffm.NativeType.adaptDowncall(
            <raw>,
            <return><suffix>)
        """
        .replace("<raw>", rawHandle.replace("\n", "\n    "))
        .replace("<return>", returnGenerator.needsDowncallAdaptation()
            ? returnGenerator.canonicalRuntimeType() : "null")
        .replace("<suffix>", suffix)
        .stripTrailing();
  }

  private boolean needsDowncallAdaptation() {
    return returnGenerator.needsDowncallAdaptation()
        || parameterGenerators.stream()
            .anyMatch(TypeGenerator::needsDowncallAdaptation);
  }

  private String methodBody(String methodHandleExpression) {
    if (!allocationPlan.shared())
      return Stream.of(paramInitializers(),
          invoke(methodHandleExpression))
          .flatMap(identity())
          .collect(joining("\n      ", "", ""));

    return Stream.of(
        plannedPreparations(),
        allocationPlanSource().lines(),
        plannedInitializers(),
        invoke(methodHandleExpression))
        .flatMap(identity())
        .collect(joining("\n      ", "", ""));
  }

  private String name(ExecutableElement method) {
    var name = method.getAnnotation(Symbol.class);

    return name == null
        ? method.getSimpleName().toString()
        : name.value();
  }

  String descriptor() {
    var returnType = element.getReturnType();
    boolean isVoid = returnType.getKind() == TypeKind.VOID;

    var layouts = Stream.of(
        isVoid ? Stream.<String> empty() : Stream.of(returnGenerator.layout()),
        leadingNativeArguments.stream().map(NativeArgument::layout),
        nativeParameters.stream().map(VariableGenerator::argumentLayout))
        .flatMap(identity());

    String prefix = isVoid
        ? "java.lang.foreign.FunctionDescriptor.ofVoid("
        : "java.lang.foreign.FunctionDescriptor.of(";

    String newLine = "\n          ";

    return layouts
        .collect(joining("," + newLine, prefix + newLine, ")"));
  }

  String downcallDescriptor() {
    return descriptor().replace("\n          ", "\n        ");
  }

  String downcallOptions() {
    return linkerOptions().replace("\n          ", "\n    ");
  }

  String signature() {
    String prefix = "public "
        + returnTypeName() + " " + element.getSimpleName() + "(";

    String newLine = "\n      ";

    return parameterGenerators.stream()
        .map(VariableGenerator::signature)
        .collect(joining("," + newLine, prefix + newLine, ")"));
  }

  String returnTypeName() {
    if (returnGenerator.isForeignMemoryImplementation())
      return returnGenerator.typeName();

    if (returnGenerator.isCFString())
      return returnGenerator.typeName();

    if (returnGenerator.isPrimitive())
      return returnGenerator.typeName();

    return element.getReturnType().toString();
  }

  String bridgeReturnTypeName() {
    return returnGenerator.bridgeTypeName();
  }

  String linkerOptions() {
    var variadic = Stream.ofNullable(
        element.getAnnotation(FirstVariadicArg.class))
        .map(annotation -> "java.lang.foreign.Linker.Option"
            + ".firstVariadicArg("
            + (annotation.value() + leadingNativeArguments.size()) + ")");

    var callState = callStateParameters.stream()
        .findFirst()
        .stream()
        .map(parameter -> parameter.foreignMemoryClassName()
            + ".LinkerOption$F");

    return Stream.concat(variadic, callState)
        .map(option -> ",\n          " + option)
        .collect(joining());
  }

  private String confinedArena() {
    return allocationPlan.confinedArena()
        ? "(var arena$f = java.lang.foreign.Arena.ofConfined()) " : "";
  }

  private Stream<String> invoke(String methodHandleExpression) {
    var call = methodHandleExpression + ".invokeExact("
        + params() + ")";
    var copyOut = copyOut().toList();

    if (returnGenerator.isPrimitiveAddress())
      return primitiveAddressInvoke(call, copyOut);

    if (returnGenerator.isPrimitive())
      return returnWithCopyOut(
          "(" + returnGenerator.typeName() + ") " + call, copyOut);

    if (returnGenerator.isMemorySegment())
      return returnWithCopyOut(
          "(java.lang.foreign.MemorySegment) " + call, copyOut);

    if (returnGenerator.isRecord())
      return returnWithCopyOut(recordExpression(call), copyOut);

    if (returnGenerator.isCFString())
      return cfStringInvoke(call, copyOut);

    if (returnGenerator.isString())
      return stringInvoke(call, copyOut);

    if (returnGenerator.isForeignMemoryImplementation())
      return returnWithCopyOut(foreignMemoryExpression(call), copyOut);

    if (returnGenerator.isForeignMemory())
      return returnWithCopyOut(foreignMemoryExpression(call), copyOut);

    // returnType.getKind() == TypeKind.VOID
    return statementWithCopyOut(call, copyOut);
  }

  /// The descriptor excludes Java-only allocator and capture parameters, but
  /// the handle takes them before the native arguments. Adaptation and invocation
  /// must use this same order.
  private Stream<String> downcallArguments(String recordAllocator,
      Function<VariableGenerator, String> syntheticArgument,
      Function<NativeArgument, String> leadingArgument,
      Function<VariableGenerator, String> nativeArgument) {
    return Stream.of(
        Stream.ofNullable(returnGenerator.isRecord() && returnGenerator.isValue()
            ? recordAllocator : null),
        allocatorParameters.stream().map(syntheticArgument),
        callStateParameters.stream().map(syntheticArgument),
        leadingNativeArguments.stream().map(leadingArgument),
        nativeParameters.stream().map(nativeArgument))
        .flatMap(identity());
  }

  private String params() {
    String newLine = "\n    ";
    var recordAllocator = "(java.lang.foreign.SegmentAllocator) "
        + (allocationPlan.shared()
            ? "java.lang.foreign.SegmentAllocator.prefixAllocator("
                + "return$allocation$f)"
            : "arena$f");

    return downcallArguments(recordAllocator,
        VariableGenerator::invoke, NativeArgument::expression,
        allocationPlan.shared()
            ? VariableGenerator::plannedInvoke : VariableGenerator::invoke)
        .collect(joining("," + newLine, newLine, ""));
  }

  private Stream<String> returnWithCopyOut(
      String expression, List<String> copyOut) {
    if (copyOut.isEmpty())
      return ("return " + expression + ";").lines();

    var all = Stream.of(
        ("var result$f = " + expression + ";").lines(),
        copyOut.stream(),
        Stream.of("return result$f;"));

    return all.flatMap(identity());
  }

  private Stream<String> statementWithCopyOut(
      String statement, List<String> copyOut) {
    var base = (statement + ";").lines();
    if (copyOut.isEmpty()) return base;

    return Stream.of(base, copyOut.stream()).flatMap(identity());
  }

  private String recordExpression(String call) {
    String className = returnGenerator.foreignMemoryClassName();

    return returnGenerator.isValue()
        ? className + ".fromMemorySegment$F("
            + "(java.lang.foreign.MemorySegment) "
            + call + ")"
        : className + ".reinterpret$F((java.lang.foreign.MemorySegment) "
            + call + ")";
  }

  private Stream<String> stringInvoke(String call, List<String> copyOut) {
    var all = Stream.of(
        ("var stringResult$f = (java.lang.foreign.MemorySegment) "
            + call + ";").lines(),
        copyOut.stream(),
        """
            return stringResult$f.equals(java.lang.foreign.MemorySegment.NULL) ? null
                : stringResult$f.reinterpret(Long.MAX_VALUE).getString(0L);
            """
            .stripTrailing()
            .lines());

    return all.flatMap(identity());
  }

  private Stream<String> cfStringInvoke(String call, List<String> copyOut) {
    var result = "(java.lang.foreign.MemorySegment) " + call;

    if (!returnGenerator.isOwnedCFString())
      return returnWithCopyOut(
          "org.alveolo.ffm.macos.CFStringSupport.toJavaString(" + result + ")",
          copyOut);

    var conversion = returnWithCopyOut(
        """
            org.alveolo.ffm.macos.CFStringSupport
                .toJavaString(cfStringResult$f)
            """
            .stripTrailing(),
        copyOut);

    return """
        var cfStringResult$f = <result>;
        try {
        <conversion>
        } finally {
          org.alveolo.ffm.macos.CFStringSupport.release(cfStringResult$f);
        }
        """
        .stripTrailing()
        .replace("<result>", result)
        .replace("<conversion>", conversion.collect(joining("\n  ", "  ", "")))
        .lines();
  }

  private Stream<String> primitiveAddressInvoke(
      String call, List<String> copyOut) {
    var layout = returnGenerator.valueLayout();
    var result = returnGenerator.hasCanonicalScalar()
        ? returnGenerator.canonicalGet(
            "addressResult$f.reinterpret(" + layout + ".byteSize())", "0L")
        : """
            addressResult$f.reinterpret(<layout>.byteSize())
                .get(<layout>, 0L)
            """
            .replace("<layout>", layout)
            .stripTrailing();
    var all = Stream.of(
        ("var addressResult$f = (java.lang.foreign.MemorySegment) "
            + call + ";").lines(),
        copyOut.stream(),
        Stream.of("org.alveolo.ffm.ForeignUtils.requireNonNullAddress("
            + "addressResult$f);"),
        "return <result>;"
            .stripTrailing()
            .replace("<result>", result)
            .lines());

    return all.flatMap(identity());
  }

  private String foreignMemoryExpression(String call) {
    String className = returnGenerator.foreignMemoryClassName();

    return returnGenerator.isValue()
        ? "new " + className + "((java.lang.foreign.MemorySegment) "
            + call + ")"
        : className + ".reinterpret$F((java.lang.foreign.MemorySegment) "
            + call + ")";
  }

  private String declarations() {
    var declarations = parameterGenerators.stream()
        .filter(VariableGenerator::isCFString)
        .map(p -> "java.lang.foreign.MemorySegment " + p.cfStringName()
            + " = java.lang.foreign.MemorySegment.NULL;")
        .toList();

    if (declarations.isEmpty()) return "";

    return declarations.stream()
        .collect(joining("\n    ", "    ", "\n"));
  }

  private Stream<String> paramInitializers() {
    return parameterGenerators.stream().flatMap(this::paramInitializers);

  }

  private AllocationPlan planAllocations() {
    if (hasErrors) return new AllocationPlan(false, List.of(), false);

    var confinedArena = returnGenerator.isRecord() && returnGenerator.isValue()
        || parameterGenerators.stream()
            .anyMatch(VariableGenerator::needsConfinedArena);
    var allocations = confinedArena
        ? localAllocations() : List.<LocalAllocation>of();

    // Such converters can allocate a runtime-dependent record graph. Keep the
    // direct-arena fallback instead of guessing a backing capacity.
    var shared = allocations.size() >= 2 && parameterGenerators.stream()
        .noneMatch(this::converterNeedsAllocator);
    return new AllocationPlan(confinedArena, allocations, shared);
  }

  private boolean converterNeedsAllocator(VariableGenerator parameter) {
    if (parameter.isRecord())
      return memoryAnalyzer.recordConverterNeedsAllocator(
          parameter.typeElement);

    if (!parameter.isValueStructRecordArray()) return false;

    return memoryAnalyzer.recordConverterNeedsAllocator(
        parameter.arrayComponentGenerator().typeElement);
  }

  private Stream<String> plannedPreparations() {
    return parameterGenerators.stream()
        .map(VariableGenerator::preparation)
        .filter(not(String::isEmpty))
        .flatMap(String::lines);
  }

  private Stream<String> plannedInitializers() {
    return parameterGenerators.stream()
        .flatMap(parameter -> parameter.needsLocalAllocation()
            ? parameter.initializer(
                plannedAllocationSegment(parameter)).lines()
            : paramInitializers(parameter));
  }

  private String plannedAllocationSegment(VariableGenerator parameter) {
    return """
        allocation$MemorySegment$f.asSlice(
            <name>$allocationOffset$f, <size>)
        """
        .replace("<name>", parameter.name())
        .replace("<size>", allocationPlan.allocation(parameter.name()).byteSize())
        .strip();
  }

  private String allocationPlanSource() {
    var allocations = allocationPlan.allocations();
    if (allocations.size() < 2)
      throw new IllegalStateException(
          "Allocation plan requires at least two allocations");

    var first = allocations.getFirst();
    var plan = new StringBuilder("""
        var <name>$allocationOffset$f = 0L;
        var allocationOffset$f = <size>;
        """
        .replace("<name>", first.name())
        .replace("<size>", first.byteSize()));

    // Match slicingAllocator alignment while retaining direct slices.
    for (var allocation : allocations.subList(1, allocations.size())) {
      plan.append("""
          allocationOffset$f = Math.addExact(
              allocationOffset$f,
              Math.floorMod(-allocationOffset$f, <alignment>));
          var <name>$allocationOffset$f = allocationOffset$f;
          allocationOffset$f = Math.addExact(
              allocationOffset$f, <size>);
          """
          .replace("<name>", allocation.name())
          .replace("<size>", allocation.byteSize())
          .replace("<alignment>", allocation.alignment()));
    }

    plan.append("""
        var allocation$MemorySegment$f = allocationOffset$f == 0L
            ? java.lang.foreign.MemorySegment.NULL
            : arena$f.allocate(allocationOffset$f, <alignment>);
        """
        .replace("<alignment>", maximumAlignment(allocations)));

    for (var allocation : allocations) {
      if (!allocation.name().equals("return")) continue;

      plan.append("""
          var return$allocation$f = allocation$MemorySegment$f.asSlice(
              return$allocationOffset$f, <size>);
          """
          .replace("<size>", allocation.byteSize()));
    }

    return plan.toString().stripTrailing();
  }

  private List<LocalAllocation> localAllocations() {
    var allocations = new ArrayList<LocalAllocation>();
    parameterGenerators.stream()
        .filter(VariableGenerator::needsLocalAllocation)
        .map(parameter -> new LocalAllocation(
            parameter.name(),
            parameter.allocationByteSize(),
            parameter.allocationAlignment()))
        .forEach(allocations::add);

    if (returnGenerator.isRecord() && returnGenerator.isValue()) {
      var layout = returnGenerator.foreignMemoryClassName()
          + ".MemoryLayout$F";
      // A Java keyword cannot collide with a parameter name.
      allocations.add(new LocalAllocation(
          "return",
          layout + ".byteSize()",
          layout + ".byteAlignment()"));
    }

    return allocations;
  }

  private String maximumAlignment(List<LocalAllocation> allocations) {
    var alignments = allocations.stream()
        .map(LocalAllocation::alignment)
        .distinct()
        .toList();
    if (alignments.size() > 1)
      alignments = alignments.stream()
          .filter(alignment -> !alignment.equals("1L"))
          .toList();

    return alignments.stream()
        .reduce((left, right) ->
            "Math.max(" + left + ", " + right + ")")
        .orElseThrow();
  }

  private Stream<String> paramInitializers(VariableGenerator p) {
    if (p.isPrimitiveAddress())
      return p.primitiveAddressInitializer().lines();

    if (p.isCFString())
      return Stream.of(p.cfStringName()
          + " = org.alveolo.ffm.macos.CFStringSupport.toCFString("
          + p.name() + ");");

    return p.isCallArrayOrBuffer()
        ? p.arrayOrBufferInitializer().lines()
        : Stream.empty();
  }

  private Stream<String> copyOut() {
    return parameterGenerators.stream()
        .filter(VariableGenerator::isCallArrayOrBuffer)
        .map(VariableGenerator::arrayOrBufferCopyOut)
        .flatMap(String::lines);
  }

  private String finallyBlock() {
    var releases = parameterGenerators.stream()
        .filter(VariableGenerator::isCFString)
        .map(p -> "      org.alveolo.ffm.macos.CFStringSupport.release("
            + p.cfStringName() + ");")
        .collect(joining("\n"));

    if (releases.isEmpty()) return "";

    return " finally {\n" + releases + "\n    }";
  }

  /// @return true if any method parameter has an unsupported type.
  boolean checkParameterTypes() {
    boolean hasUnsupported = false;

    var returnCanonicalError = returnGenerator.canonicalScalarError();
    if (returnCanonicalError != null) {
      messager.printError(returnCanonicalError, element);
      hasUnsupported = true;
    }

    var firstVariadicArg = element.getAnnotation(FirstVariadicArg.class);
    if (firstVariadicArg != null) {
      var index = firstVariadicArg.value();
      var nativeParameterCount = nativeParameters.size();

      if (index < 0 || index > nativeParameterCount) {
        messager.printError(
            "@FirstVariadicArg value must be between 0 and "
                + nativeParameterCount
                + " (the native parameter count)",
            element);
        hasUnsupported = true;
      } else {
        for (var parameter : nativeParameters.subList(index, nativeParameterCount)) {
          if (isUnpromotedVariadicType(parameter)) {
            var correction = parameter.isWCharT()
                ? "remove @WCharT and use plain int"
                : "use " + promotedVariadicType(parameter) + " instead of "
                    + parameter.typeName();
            messager.printError(
                "Variadic parameter '" + parameter.name()
                    + "' must use its C-promoted type: " + correction,
                parameter.element);
            hasUnsupported = true;
          }
        }
      }
    }

    if (returnGenerator.isCallState()) {
      messager.printError(
          "@CallState types are only supported as parameters", element);
      hasUnsupported = true;
    }

    if (callStateParameters.size() > 1) {
      for (var callState : callStateParameters) {
        messager.printError(
            "Only one @CallState parameter is allowed", callState.element);
      }
      hasUnsupported = true;
    }

    boolean needsExternalAllocator = (returnGenerator.isForeignMemory()
        || returnGenerator.isForeignMemoryImplementation())
        && !returnGenerator.isRecord() && returnGenerator.isValue();

    if (needsExternalAllocator) {
      if (parameterGenerators.isEmpty()
          || !allocatorParameters.contains(parameterGenerators.getFirst())) {
        messager.printError(
            "SegmentAllocator is expected as first parameter", element);
        return true;
      }
    }

    boolean skipExternalAllocator = needsExternalAllocator;
    for (var paramGen : parameterGenerators) {
      if (skipExternalAllocator) {
        skipExternalAllocator = false;
        continue;
      }

      if (allocatorParameters.contains(paramGen)) {
        hasUnsupported = true;

        messager.printError(
            "SegmentAllocator is not expected", paramGen.element);
        continue;
      }

      var canonicalError = paramGen.canonicalScalarError();
      if (canonicalError != null) {
        hasUnsupported = true;
        messager.printError(canonicalError, paramGen.element);
        continue;
      }

      if (paramGen.isCFString() && !paramGen.isString()) {
        hasUnsupported = true;

        messager.printError(
            "@CFString is only supported on java.lang.String",
            paramGen.element);
        continue;
      }

      if (paramGen.isOwnedCFString()) {
        hasUnsupported = true;

        messager.printError(
            "@CFString(owned = true) is only supported on return types",
            paramGen.element);
        continue;
      }

      if (paramGen.hasConflictingTransferAnnotations()) {
        hasUnsupported = true;

        messager.printError(
            "@In and @Out cannot be used together", paramGen.element);
        continue;
      }

      if (paramGen.hasInvalidSequence()) {
        hasUnsupported = true;

        messager.printError(
            "@Sequence value must be positive", paramGen.element);
        continue;
      }

      if (paramGen.isCallArrayOrBufferByValue()
          && !paramGen.hasExplicitSequence) {
        hasUnsupported = true;

        messager.printError(
            "@Value array and Buffer parameters require @Sequence to define "
                + "their fixed native layout",
            paramGen.element);
        continue;
      }

      if (paramGen.isCallArrayOrBufferByValue()
          && paramGen.hasOutAnnotation()) {
        hasUnsupported = true;

        messager.printError(
            "@Out is not supported on @Value array and Buffer parameters",
            paramGen.element);
        continue;
      }

      if (paramGen.hasSequenceOnUnsupportedType()) {
        hasUnsupported = true;

        messager.printError(
            "@Sequence is only supported on array and Buffer types",
            paramGen.element);
        continue;
      }

      if ((paramGen.hasInAnnotation() || paramGen.hasOutAnnotation())
          && !paramGen.isCallArrayOrBuffer()) {
        hasUnsupported = true;

        messager.printError(
            "@In and @Out are only supported on array and Buffer parameters",
            paramGen.element);
        continue;
      }

      if (paramGen.unsupported()) {
        hasUnsupported = true;

        messager.printError(
            "Type is not supported: " + paramGen.typeName(), paramGen.element);
      }
    }

    boolean hasArrayOrBufferReturn = returnGenerator.isArray()
        || returnGenerator.isNioBuffer();

    if (hasArrayOrBufferReturn) {
      hasUnsupported = true;

      messager.printError(
          "Array and Buffer return types are not supported; "
              + "use MemorySegment for native pointer returns",
          element);
    } else if (returnCanonicalError == null
        && element.getReturnType().getKind() != TypeKind.VOID
        && returnGenerator.unsupported()) {
      hasUnsupported = true;

      messager.printError(
          "Type is not supported: " + returnGenerator.typeName(), element);
    }

    if (returnGenerator.isCFString() && !returnGenerator.isString()) {
      hasUnsupported = true;

      messager.printError(
          "@CFString is only supported on java.lang.String", element);
    }

    return hasUnsupported;
  }

  private boolean isUnpromotedVariadicType(VariableGenerator parameter) {
    if (parameter.isPrimitiveAddress()) return false;
    if (parameter.isWCharT()) return true;

    return switch (parameter.typeMirror.getKind()) {
      case BOOLEAN, BYTE, CHAR, SHORT, FLOAT -> true;
      default -> false;
    };
  }

  private String promotedVariadicType(VariableGenerator parameter) {
    return parameter.typeMirror.getKind() == TypeKind.FLOAT
        ? "double" : "int";
  }
}
