package org.alveolo.ffm.processor;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;
import static org.alveolo.ffm.processor.TypeGenerator.VALUE_LAYOUT_NOT_SUPPORTED;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import org.alveolo.ffm.Symbol;

class ExecutableGenerator {
  final ProcessingEnvironment processingEnv;
  final Types types;
  final Messager messager;
  final ExecutableElement element;
  final boolean hasErrors;
  final String methodHandleName;
  final boolean instanceMethodHandle;
  final TypeGenerator returnGenerator;
  final List<VariableGenerator> parameterGenerators;

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName) {
    this(processingEnv, element, methodHandleName, false);
  }

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName,
      boolean instanceMethodHandle) {
    this.processingEnv = processingEnv;
    types = processingEnv.getTypeUtils();
    messager = processingEnv.getMessager();
    this.element = element;
    this.methodHandleName = methodHandleName;
    this.instanceMethodHandle = instanceMethodHandle;

    returnGenerator = new TypeGenerator(processingEnv, element.getReturnType());

    parameterGenerators = element.getParameters().stream()
        .map(param -> new VariableGenerator(processingEnv, param))
        .toList();

    hasErrors = checkParameterTypes();
  }

  String method() {
    if (hasErrors)
      return """

            <signature> {
              throw new RuntimeException("Check compile errors!");
            }
          """
          .replace("<signature>", signature());

    return methodHandleDeclaration() + """

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
        .replace("<body>", methodBody())
        .replace("<finallyBlock>", finallyBlock());
  }

  private String methodHandleDeclaration() {
    if (instanceMethodHandle) return """

          private final MethodHandle <mh>;
        """
        .replace("<mh>", methodHandleName);

    return """

          private static final MethodHandle <mh> = FF$LINKER.downcallHandle(
              FF$LOOKUP.findOrThrow("<name>"),
              <descriptor>);
        """
        .replace("<mh>", methodHandleName)
        .replace("<name>", name(element))
        .replace("<descriptor>", descriptor());
  }

  private String methodBody() {
    return Stream.of(paramInitializers(), invoke())
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

    var stream = Stream.concat(
        isVoid ? Stream.empty() : Stream.of(returnGenerator),
        parameterGenerators.stream());

    String prefix = isVoid
        ? "FunctionDescriptor.ofVoid("
        : "FunctionDescriptor.of(";

    String newLine = "\n          ";

    return stream
        .filter(not(TypeGenerator::isSegmentAllocator))
        .map(TypeGenerator::layout)
        .collect(joining("," + newLine, prefix + newLine, ")"));
  }

  private String signature() {
    String prefix = "public " + returnTypeName()
        + " " + element.getSimpleName() + "(";

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

  private Stream<String> invoke() {
    var returnType = element.getReturnType();
    var call = methodHandleName + ".invokeExact(" + params() + ")";
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
      return returnWithCopyOut(call, copyOut); // TODO

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
    var paramsList = Stream.concat(
        Stream.ofNullable(needsLocalAllocator
            ? "(SegmentAllocator) ff$arena" : null),
        parameterGenerators.stream().map(VariableGenerator::invoke));

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

  private String recordExpression(
      javax.lang.model.type.TypeMirror returnType, String call) {
    var type = (TypeElement) types.asElement(returnType);

    if (returnGenerator.isValue())
      return foreignClassName(type)
          + ".fromMemorySegment((MemorySegment) " + call + ")";

    return foreignClassName(type)
        + ".reinterpret((MemorySegment) " + call + ")";
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

  private String foreignMemoryExpression(
      javax.lang.model.type.TypeMirror returnType, String call) {
    var type = (TypeElement) types.asElement(returnType);
    if (returnGenerator.isValue())
      return "new " + foreignClassName(type)
          + "((MemorySegment) " + call + ")";

    return foreignClassName(type)
        + ".reinterpret((MemorySegment) " + call + ")";
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
    if (p.isCFString()) return Stream.of(p.cfStringName()
        + " = org.alveolo.ffm.macos.CFStringSupport.toCFString("
        + p.name() + ");");

    if (p.isArrayOrBuffer())
      return p.arrayOrBufferInitializer().lines();

    return Stream.empty();
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
        processingEnv.getMessager().printError(
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

        processingEnv.getMessager().printError(
            "SegmentAllocator is not expected", paramGen.element);
        continue;
      }

      if (paramGen.isCFString() && !paramGen.isString()) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "@CFString is only supported on java.lang.String",
            paramGen.element);
        continue;
      }

      if (paramGen.isOwnedCFString()) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "@CFString(owned = true) is only supported on return types",
            paramGen.element);
        continue;
      }

      if (paramGen.hasConflictingTransferAnnotations()) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "@In and @Out cannot be used together", paramGen.element);
        continue;
      }

      if ((paramGen.hasInAnnotation() || paramGen.hasOutAnnotation())
          && !paramGen.isArrayOrBuffer()) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "@In and @Out are only supported on array and Buffer parameters",
            paramGen.element);
        continue;
      }

      String layout = paramGen.layout();

      if (layout == VALUE_LAYOUT_NOT_SUPPORTED) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "Type is not supported: " + paramGen.typeMirror, paramGen.element);
      }
    }

    if (element.getReturnType().getKind() != TypeKind.VOID
        && returnGenerator.layout() == VALUE_LAYOUT_NOT_SUPPORTED) {
      hasUnsupported = true;

      processingEnv.getMessager().printError(
          "Type is not supported: " + returnGenerator.typeMirror, element);
    }

    if (returnGenerator.isCFString() && !returnGenerator.isString()) {
      hasUnsupported = true;

      processingEnv.getMessager().printError(
          "@CFString is only supported on java.lang.String", element);
    }

    return hasUnsupported;
  }
}
