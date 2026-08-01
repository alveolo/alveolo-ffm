package org.alveolo.ffm.processor;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.Symbol;

class ExecutableGenerator {
  final Messager messager;
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

  record NativeArgument(String layout, String expression) {}
  record LocalAllocation(
      String name, String byteSize, String alignment) {}

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

    hasErrors = checkParameterTypes();
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
        .replace("    <declarations>\n", declarations())
        .replace("<confinedArena>", confinedArena())
        .replace("<body>", methodBody(methodHandleExpression))
        .replace("<finallyBlock>", finallyBlock());
  }

  String methodHandleDeclaration() {
    if (instanceMethodHandle)
      return """

            private final java.lang.invoke.MethodHandle <mh>;
          """
          .replace("<mh>", methodHandleName);

    var rawHandle = """
        <linker>.downcallHandle(
            <lookup>.findOrThrow("<name>"),
            <descriptor><options>)
        """
        .replace("<linker>", linkerExpression)
        .replace("<lookup>", lookupExpression)
        .replace("<name>", name(element))
        .replace("<descriptor>", downcallDescriptor())
        .replace("<options>", downcallOptions())
        .stripTrailing();

    return """

          private static final java.lang.invoke.MethodHandle <mh> =
              <initializer>;
        """
        .replace("<mh>", methodHandleName)
        .replace("<initializer>", adaptDowncall(rawHandle, false)
            .replace("\n", "\n      "));
  }

  String adaptDowncall(String rawHandle, boolean unbound) {
    if (!needsDowncallAdaptation()) return rawHandle;

    var arguments = Stream.of(
        Stream.ofNullable(unbound ? "null" : null),
        Stream.ofNullable(returnGenerator.isRecord()
            && returnGenerator.isValue() ? "null" : null),
        parameterGenerators.stream()
            .filter(TypeGenerator::isSegmentAllocator)
            .map(_ -> "null"),
        parameterGenerators.stream()
            .filter(TypeGenerator::isCallState)
            .map(_ -> "null"),
        leadingNativeArguments.stream().map(_ -> "null"),
        parameterGenerators.stream()
            .filter(not(TypeGenerator::isSegmentAllocator))
            .filter(not(TypeGenerator::isCallState))
            .map(parameter -> parameter.needsDowncallAdaptation()
                ? parameter.canonicalRuntimeType() : "null"))
        .flatMap(identity())
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
    if (!canPlanAllocations())
      return Stream.of(paramInitializers(),
          invoke(methodHandleExpression, false))
          .flatMap(identity())
          .collect(joining("\n      ", "", ""));

    return Stream.of(
        plannedPreparations(),
        allocationPlan().lines(),
        plannedInitializers(),
        invoke(methodHandleExpression, true))
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
        parameterGenerators.stream()
            .filter(not(TypeGenerator::isSegmentAllocator))
            .filter(not(TypeGenerator::isCallState))
            .map(VariableGenerator::argumentLayout))
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

    var callState = parameterGenerators.stream()
        .filter(TypeGenerator::isCallState)
        .findFirst()
        .stream()
        .map(parameter -> parameter.foreignMemoryClassName()
            + ".LinkerOption$F");

    return Stream.concat(variadic, callState)
        .map(option -> ",\n          " + option)
        .collect(joining());
  }

  private String confinedArena() {
    return needsConfinedArena()
        ? "(var arena$f = java.lang.foreign.Arena.ofConfined()) " : "";
  }

  private Stream<String> invoke(
      String methodHandleExpression, boolean plannedAllocations) {
    var call = methodHandleExpression + ".invokeExact("
        + params(plannedAllocations) + ")";
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

  private String params(boolean plannedAllocations) {
    String newLine = "\n    ";

    boolean needsLocalAllocator =
        returnGenerator.isRecord() && returnGenerator.isValue();

    // SegmentAllocator parameters are part of the downcall argument list only
    // when an external allocator is required. Keep validation in sync so an
    // allocator parameter is rejected unless it is passed here.
    var paramsList = Stream.of(
        Stream.ofNullable(needsLocalAllocator
            ? "(java.lang.foreign.SegmentAllocator) "
                + (plannedAllocations
                    ? "java.lang.foreign.SegmentAllocator.prefixAllocator("
                        + "result$allocation$f)"
                    : "arena$f")
            : null),
        parameterGenerators.stream()
            .filter(TypeGenerator::isSegmentAllocator)
            .map(VariableGenerator::invoke),
        parameterGenerators.stream()
            .filter(TypeGenerator::isCallState)
            .map(VariableGenerator::invoke),
        leadingNativeArguments.stream().map(NativeArgument::expression),
        parameterGenerators.stream()
            .filter(not(TypeGenerator::isSegmentAllocator))
            .filter(not(TypeGenerator::isCallState))
            .map(plannedAllocations
                ? VariableGenerator::plannedInvoke
                : VariableGenerator::invoke))
        .flatMap(identity());

    return paramsList.collect(joining("," + newLine, newLine, ""));
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
            return stringResult$f.address() == 0L ? null
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

  boolean needsConfinedArena() {
    return returnGenerator.isRecord() && returnGenerator.isValue()
        || parameterGenerators.stream()
            .anyMatch(VariableGenerator::needsConfinedArena);
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

  private boolean canPlanAllocations() {
    if (!needsConfinedArena() || localAllocations().size() < 2)
      return false;

    // Such converters can allocate a runtime-dependent record graph. Keep the
    // direct-arena fallback instead of guessing a backing capacity.
    return parameterGenerators.stream()
        .noneMatch(this::converterNeedsAllocator);
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
        .map(VariableGenerator::plannedPreparation)
        .filter(not(String::isEmpty))
        .flatMap(String::lines);
  }

  private Stream<String> plannedInitializers() {
    return parameterGenerators.stream()
        .filter(VariableGenerator::needsLocalAllocation)
        .map(parameter -> parameter.plannedInitializer(
            plannedAllocationSegment(parameter)))
        .flatMap(String::lines);
  }

  private String plannedAllocationSegment(VariableGenerator parameter) {
    return """
        allocation$MemorySegment$f.asSlice(
            <name>$allocationOffset$f, <size>)
        """
        .replace("<name>", parameter.name())
        .replace("<size>", parameter.allocationByteSize())
        .strip();
  }

  private String allocationPlan() {
    var allocations = localAllocations();
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
        var allocation$MemorySegment$f = arena$f.allocate(
            allocationOffset$f, <alignment>);
        """
        .replace("<alignment>", maximumAlignment(allocations)));

    for (var allocation : allocations) {
      if (!allocation.name().equals("result")) continue;

      plan.append("""
          var result$allocation$f = allocation$MemorySegment$f.asSlice(
              result$allocationOffset$f, <size>);
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
      allocations.add(new LocalAllocation(
          "result",
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
      var nativeParameterCount = (int) parameterGenerators.stream()
          .filter(not(TypeGenerator::isSegmentAllocator))
          .filter(not(TypeGenerator::isCallState))
          .count();

      if (index < 0 || index > nativeParameterCount) {
        messager.printError(
            "@FirstVariadicArg value must be between 0 and "
                + nativeParameterCount
                + " (the native parameter count)",
            element);
        hasUnsupported = true;
      } else {
        var nativeIndex = 0;
        for (var parameter : parameterGenerators) {
          if (parameter.isSegmentAllocator() || parameter.isCallState()) {
            continue;
          }

          if (nativeIndex >= index
              && isUnpromotedVariadicType(parameter)) {
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
          nativeIndex++;
        }
      }
    }

    if (returnGenerator.isCallState()) {
      messager.printError(
          "@CallState types are only supported as parameters", element);
      hasUnsupported = true;
    }

    var callStates = parameterGenerators.stream()
        .filter(TypeGenerator::isCallState)
        .toList();
    if (callStates.size() > 1) {
      for (var callState : callStates) {
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
          || !parameterGenerators.get(0).isSegmentAllocator()) {
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

      if (paramGen.isSegmentAllocator()) {
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

      if (paramGen.hasConflictingSizeAnnotations()) {
        hasUnsupported = true;

        messager.printError(
            "@CountedBy and @Sequence cannot be used together",
            paramGen.element);
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

      if (paramGen.hasCountedBy()) {
        if (!paramGen.isCallArrayOrBuffer()) {
          hasUnsupported = true;

          messager.printError(
              "@CountedBy is only supported on primitive arrays, NIO Buffer "
                  + "types, and value-style @Struct record arrays",
              paramGen.element);
          continue;
        }

        var countParam = parameterGenerators.stream()
            .filter(p -> p.name().equals(paramGen.countedByName()))
            .findFirst();

        if (countParam.isEmpty()) {
          hasUnsupported = true;

          messager.printError(
              "@CountedBy(\"" + paramGen.countedByName()
                  + "\") does not name a parameter of this method",
              paramGen.element);
          continue;
        }

        if (!countParam.orElseThrow().isCountType()) {
          hasUnsupported = true;

          messager.printError(
              "@CountedBy parameter '" + paramGen.countedByName()
                  + "' must be a plain scalar of type byte, short, int, "
                  + "or long",
              paramGen.element);
          continue;
        }
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
