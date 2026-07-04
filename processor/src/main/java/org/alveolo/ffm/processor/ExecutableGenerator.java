package org.alveolo.ffm.processor;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemoryClassName;
import static org.alveolo.ffm.processor.TypeGenerator.VALUE_LAYOUT_NOT_SUPPORTED;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.alveolo.ffm.Symbol;

class ExecutableGenerator {
  final ProcessingEnvironment processingEnv;
  final Messager messager;
  final Elements elements;
  final Types types;
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
      ExecutableElement element, String methodHandleName) {
    this(processingEnv, element, methodHandleName, false);
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle) {
    this(processingEnv, element, methodHandleName,
        instanceMethodHandle, List.of());
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle,
      List<NativeArgument> leadingNativeArguments) {
    this(processingEnv, element, methodHandleName, instanceMethodHandle,
        leadingNativeArguments, "FF$LINKER", "FF$LOOKUP");
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle,
      List<NativeArgument> leadingNativeArguments,
      String linkerExpression, String lookupExpression) {
    this.processingEnv = processingEnv;
    messager = processingEnv.getMessager();
    elements = processingEnv.getElementUtils();
    types = processingEnv.getTypeUtils();
    this.element = element;
    this.methodHandleName = methodHandleName;
    this.instanceMethodHandle = instanceMethodHandle;
    this.leadingNativeArguments = leadingNativeArguments;
    this.linkerExpression = linkerExpression;
    this.lookupExpression = lookupExpression;

    returnGenerator = new TypeGenerator(processingEnv, element.getReturnType());

    parameterGenerators = element.getParameters().stream()
        .map(param -> new VariableGenerator(processingEnv, param))
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

          <signature> {<declarations>
            try <confinedArena>{
              <body>
            } catch (RuntimeException|Error ff$e) {
              throw ff$e;
            } catch (Throwable ff$t) {
              throw new AssertionError(ff$t);
            }<finallyBlock>
          }
        """
        .replace("<signature>", signature())
        .replace("<declarations>", declarations())
        .replace("<confinedArena>", confinedArena())
        .replace("<body>", methodBody(methodHandleExpression))
        .replace("<finallyBlock>", finallyBlock());
  }

  String methodHandleDeclaration() {
    if (instanceMethodHandle)
      return """

            private final MethodHandle <mh>;
          """
          .replace("<mh>", methodHandleName);

    return """

          private static final MethodHandle <mh> = <linker>.downcallHandle(
              <lookup>.findOrThrow("<name>"),
              <descriptor>);
        """
        .replace("<mh>", methodHandleName)
        .replace("<linker>", linkerExpression)
        .replace("<lookup>", lookupExpression)
        .replace("<name>", name(element))
        .replace("<descriptor>", descriptor());
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
            .map(TypeGenerator::layout))
        .flatMap(identity());

    String prefix = isVoid
        ? "FunctionDescriptor.ofVoid("
        : "FunctionDescriptor.of(";

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

  private String returnTypeName() {
    if (returnGenerator.isCFString())
      return returnGenerator.typeName();

    return element.getReturnType().toString();
  }

  private String confinedArena() {
    return needsConfinedArena() ? "(var ff$arena = Arena.ofConfined()) " : "";
  }

  private Stream<String> invoke(String methodHandleExpression) {
    var returnType = element.getReturnType();
    var call = methodHandleExpression + ".invokeExact(" + params() + ")";
    var copyOut = copyOut().toList();

    if (returnGenerator.isPrimitive())
      return returnWithCopyOut("(" + returnType + ") " + call, copyOut);

    if (returnGenerator.isMemorySegment())
      return returnWithCopyOut("(MemorySegment) " + call, copyOut);

    if (returnGenerator.isRecord())
      return returnWithCopyOut(recordExpression(returnType, call), copyOut);

    if (returnGenerator.isCFString())
      return cfStringInvoke(call, copyOut);

    if (returnGenerator.isString())
      return stringInvoke(call, copyOut);

    if (returnGenerator.isForeignMemory())
      return returnWithCopyOut(foreignMemoryExpression(returnType, call),
          copyOut);

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
            ? "(SegmentAllocator) ff$arena" : null),
        leadingNativeArguments.stream().map(NativeArgument::expression),
        parameterGenerators.stream().map(VariableGenerator::invoke))
        .flatMap(identity());

    return paramsList.collect(joining("," + newLine, newLine, ""));
  }

  private Stream<String> returnWithCopyOut(
      String expression, List<String> copyOut) {
    if (copyOut.isEmpty())
      return ("return " + expression + ";").lines();

    var all = Stream.of(
        ("var ff$result = " + expression + ";").lines(),
        copyOut.stream(),
        Stream.of("return ff$result;"));

    return all.flatMap(identity());
  }

  private Stream<String> statementWithCopyOut(
      String statement, List<String> copyOut) {
    var base = (statement + ";").lines();
    if (copyOut.isEmpty()) return base;

    return Stream.of(base, copyOut.stream()).flatMap(identity());
  }

  private String recordExpression(TypeMirror returnType, String call) {
    var type = (TypeElement) types.asElement(returnType);
    String className = foreignMemoryClassName(type, elements);

    return returnGenerator.isValue()
        ? className + ".fromMemorySegment((MemorySegment) " + call + ")"
        : className + ".reinterpret((MemorySegment) " + call + ")";
  }

  private Stream<String> stringInvoke(String call, List<String> copyOut) {
    var all = Stream.of(
        ("var ff$string$r = (MemorySegment) " + call + ";").lines(),
        copyOut.stream(),
        """
            return ff$string$r.address() == 0L ? null
                : ff$string$r.reinterpret(Long.MAX_VALUE).getString(0L);"""
            .lines());

    return all.flatMap(identity());
  }

  private Stream<String> cfStringInvoke(String call, List<String> copyOut) {
    var result = "(MemorySegment) " + call;

    if (!returnGenerator.isOwnedCFString())
      return returnWithCopyOut(
          "org.alveolo.ffm.macos.CFStringSupport.toJavaString(" + result + ")",
          copyOut);

    var conversion = returnWithCopyOut("""
        org.alveolo.ffm.macos.CFStringSupport
            .toJavaString(ff$CFString$r)""", copyOut);

    return """
        var ff$CFString$r = <result>;
        try {
        <conversion>
        } finally {
          org.alveolo.ffm.macos.CFStringSupport.release(ff$CFString$r);
        }"""
        .replace("<result>", result)
        .replace("<conversion>", conversion.collect(joining("\n  ", "  ", "")))
        .lines();
  }

  private String foreignMemoryExpression(TypeMirror returnType, String call) {
    var type = (TypeElement) types.asElement(returnType);
    String className = foreignMemoryClassName(type, elements);

    return returnGenerator.isValue()
        ? "new " + className + "((MemorySegment) " + call + ")"
        : className + ".reinterpret((MemorySegment) " + call + ")";
  }

  boolean needsConfinedArena() {
    return returnGenerator.isRecord() && returnGenerator.isValue()
        || parameterGenerators.stream()
            .anyMatch(VariableGenerator::needsConfinedArena);
  }

  private String declarations() {
    var declarations = parameterGenerators.stream()
        .filter(VariableGenerator::isCFString)
        .map(p -> "MemorySegment " + p.cfStringName()
            + " = MemorySegment.NULL;")
        .toList();

    if (declarations.isEmpty()) return "";

    return declarations.stream()
        .collect(joining("\n    ", "\n    ", ""));
  }

  private Stream<String> paramInitializers() {
    return parameterGenerators.stream().flatMap(this::paramInitializers);

  }

  private Stream<String> paramInitializers(VariableGenerator p) {
    if (p.isCFString())
      return Stream.of(p.cfStringName()
          + " = org.alveolo.ffm.macos.CFStringSupport.toCFString("
          + p.name() + ");");

    return p.isArrayOrBuffer() ? p.arrayOrBufferInitializer().lines()
        : Stream.empty();
  }

  private Stream<String> copyOut() {
    return parameterGenerators.stream()
        .filter(VariableGenerator::isArrayOrBuffer)
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

  /**
   * @return true if any of the method parameters has unsupported type
   */
  boolean checkParameterTypes() {
    boolean hasUnsupported = false;

    boolean needsExternalAllocator = returnGenerator.isForeignMemory()
        && !returnGenerator.isRecord() && returnGenerator.isValue();

    if (needsExternalAllocator) {
      if (parameterGenerators.isEmpty()
          || !parameterGenerators.get(0).isSegmentAllocator()) {
        messager.printError(
            "SegmentAllocator is expected as first parameter",
            element);
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

      if ((paramGen.hasInAnnotation() || paramGen.hasOutAnnotation())
          && !paramGen.isArrayOrBuffer()) {
        hasUnsupported = true;

        messager.printError(
            "@In and @Out are only supported on array and Buffer parameters",
            paramGen.element);
        continue;
      }

      String layout = paramGen.layout();

      if (layout == VALUE_LAYOUT_NOT_SUPPORTED) {
        hasUnsupported = true;

        messager.printError(
            "Type is not supported: " + paramGen.typeMirror, paramGen.element);
      }
    }

    if (element.getReturnType().getKind() != TypeKind.VOID
        && returnGenerator.layout() == VALUE_LAYOUT_NOT_SUPPORTED) {
      hasUnsupported = true;

      messager.printError(
          "Type is not supported: " + returnGenerator.typeMirror, element);
    }

    if (returnGenerator.isCFString() && !returnGenerator.isString()) {
      hasUnsupported = true;

      messager.printError(
          "@CFString is only supported on java.lang.String", element);
    }

    return hasUnsupported;
  }
}
