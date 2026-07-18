package org.alveolo.ffm.processor;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

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

  record NativeArgument(String layout, String expression) {}

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

    return """

          private static final java.lang.invoke.MethodHandle <mh> =
              <linker>.downcallHandle(
              <lookup>.findOrThrow("<name>"),
              <descriptor><options>);
        """
        .replace("<mh>", methodHandleName)
        .replace("<linker>", linkerExpression)
        .replace("<lookup>", lookupExpression)
        .replace("<name>", name(element))
        .replace("<descriptor>", descriptor())
        .replace("<options>", linkerOptions());
  }

  private String methodBody(String methodHandleExpression) {
    return Stream.of(paramInitializers(), invoke(methodHandleExpression))
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
    return parameterGenerators.stream()
        .filter(TypeGenerator::isCallState)
        .findFirst()
        .map(parameter -> ",\n          "
            + parameter.foreignMemoryClassName() + ".LinkerOption$F")
        .orElse("");
  }

  private String confinedArena() {
    return needsConfinedArena()
        ? "(var arena$f = java.lang.foreign.Arena.ofConfined()) " : "";
  }

  private Stream<String> invoke(String methodHandleExpression) {
    var returnType = element.getReturnType();
    var call = methodHandleExpression + ".invokeExact(" + params() + ")";
    var copyOut = copyOut().toList();

    if (returnGenerator.isPrimitiveAddress())
      return primitiveAddressInvoke(call, copyOut);

    if (returnGenerator.isPrimitive())
      return returnWithCopyOut("(" + returnType + ") " + call, copyOut);

    if (returnGenerator.isMemorySegment())
      return returnWithCopyOut(
          "(java.lang.foreign.MemorySegment) " + call, copyOut);

    if (returnGenerator.isRecord())
      return returnWithCopyOut(recordExpression(returnType, call), copyOut);

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

  private String params() {
    String newLine = "\n    ";

    boolean needsLocalAllocator =
        returnGenerator.isRecord() && returnGenerator.isValue();

    // SegmentAllocator parameters are part of the downcall argument list only
    // when an external allocator is required. Keep validation in sync so an
    // allocator parameter is rejected unless it is passed here.
    var paramsList = Stream.of(
        Stream.ofNullable(needsLocalAllocator
            ? "(java.lang.foreign.SegmentAllocator) arena$f" : null),
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
            .map(VariableGenerator::invoke))
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

  private String recordExpression(TypeMirror returnType, String call) {
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
    var all = Stream.of(
        ("var addressResult$f = (java.lang.foreign.MemorySegment) "
            + call + ";").lines(),
        copyOut.stream(),
        """
            return addressResult$f.reinterpret(<layout>.byteSize())
                .get(<layout>, 0L);
            """
            .stripTrailing()
            .replace("<layout>", layout)
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
    } else if (element.getReturnType().getKind() != TypeKind.VOID
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
}
