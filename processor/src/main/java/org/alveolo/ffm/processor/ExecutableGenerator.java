package org.alveolo.ffm.processor;

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

import org.alveolo.ffm.ForeignName;

class ExecutableGenerator {
  final ProcessingEnvironment processingEnv;
  final Types types;
  final Messager messager;
  final ExecutableElement element;
  final boolean hasErrors;
  final String methodHandleName;
  final TypeGenerator returnGenerator;
  final List<VariableGenerator> parameterGenerators;

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName) {
    this.processingEnv = processingEnv;
    types = processingEnv.getTypeUtils();
    messager = processingEnv.getMessager();
    this.element = element;
    this.methodHandleName = methodHandleName;

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

    return """

          private static final MethodHandle <mh> = FF$LINKER.downcallHandle(
              FF$LOOKUP.find("<name>").get(),
              <descriptor>);

          <signature> {
          <cfStringDeclarations>    try <allocator>{
          <cfStringInitializers>      <invoke>
            } catch (RuntimeException|Error ff$e) {
              throw ff$e;
            } catch (Throwable ff$t) {
              throw new AssertionError(ff$t);
            }<cfStringFinally>
          }
        """
        .replace("<mh>", methodHandleName)
        .replace("<name>", name(element))
        .replace("<descriptor>", descriptor())
        .replace("<signature>", signature())
        .replace("<cfStringDeclarations>", cfStringDeclarations())
        .replace("<allocator>", allocatorDefinition())
        .replace("<cfStringInitializers>", cfStringInitializers())
        .replace("<cfStringFinally>", cfStringFinally())
        .replace("<invoke>", invoke());
  }

  private String name(ExecutableElement method) {
    var name = method.getAnnotation(ForeignName.class);

    return name == null
        ? method.getSimpleName().toString()
        : name.value();
  }

  private String descriptor() {
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
    if (returnGenerator.isCFString()) return returnGenerator.typeName();

    return element.getReturnType().toString();
  }

  private String allocatorDefinition() {
    return needsConfinedArena() ? "(var ff$arena = Arena.ofConfined()) " : "";
  }

  private String invoke() {
    var returnType = element.getReturnType();

    String newLine = "\n          ";

    boolean needsLocalAllocator =
        returnGenerator.isRecord() && returnGenerator.isValue();
    // SegmentAllocator parameters are part of the downcall argument list only
    // when an external allocator is required. Keep validation in sync so an
    // allocator parameter is rejected unless it is passed here.
    var paramsList = Stream.concat(
        Stream.ofNullable(needsLocalAllocator
            ? "(SegmentAllocator) ff$arena" : null),
        parameterGenerators.stream().map(VariableGenerator::invoke));

    var params = paramsList.collect(joining("," + newLine, newLine, ""));

    if (returnGenerator.isPrimitive())
      return "return (" + returnType + ") " + methodHandleName
          + ".invokeExact(" + params + ");";

    if (returnGenerator.isMemorySegment())
      return "return (MemorySegment) " + methodHandleName
          + ".invokeExact(" + params + ");";

    if (returnGenerator.isRecord()) {
      var type = (TypeElement) types.asElement(returnType);
      if (returnGenerator.isValue())
        return "return " + foreignClassName(type)
            + ".fromMemorySegment((MemorySegment) " + methodHandleName
            + ".invokeExact(" + params + "));";

      return "return " + foreignClassName(type)
          + ".fromMemorySegment(((MemorySegment) " + methodHandleName
          + ".invokeExact(" + params + ")).reinterpret("
          + foreignClassName(type) + ".FM$LAYOUT.byteSize()));";
    }

    if (returnGenerator.isCFString()) {
      var result = "(MemorySegment) " + methodHandleName
          + ".invokeExact(" + params + ")";

      if (!returnGenerator.isOwnedCFString())
        return "return org.alveolo.ffm.macos.CFStringSupport.toJavaString("
            + result + ");";

      return """
          var ff$CFString$r = <result>;
                try {
                  return org.alveolo.ffm.macos.CFStringSupport
                      .toJavaString(ff$CFString$r);
                } finally {
                  org.alveolo.ffm.macos.CFStringSupport.release(ff$CFString$r);
                }"""
          .replace("<result>", result);
    }

    if (returnGenerator.isString())
      return "return " + methodHandleName + ".invokeExact(" + params + ");"; // TODO

    if (returnGenerator.isForeignMemory()) {
      var type = (TypeElement) types.asElement(returnType);
      if (returnGenerator.isValue())
        return "return new " + foreignClassName(type)
            + "((MemorySegment) " + methodHandleName
            + ".invokeExact(" + params + "));";

      return "return new " + foreignClassName(type)
          + "(((MemorySegment) " + methodHandleName
          + ".invokeExact(" + params + ")).reinterpret("
          + foreignClassName(type) + ".FM$LAYOUT.byteSize()));";
    }

    // returnType.getKind() == TypeKind.VOID
    return methodHandleName + ".invokeExact(" + params + ");";
  }

  boolean needsConfinedArena() {
    return returnGenerator.isRecord() && returnGenerator.isValue()
        || parameterGenerators.stream()
            .anyMatch(VariableGenerator::needsConfinedArena);
  }

  private String cfStringDeclarations() {
    var declarations = parameterGenerators.stream()
        .filter(VariableGenerator::isCFString)
        .map(p -> "MemorySegment " + p.cfStringName()
            + " = MemorySegment.NULL;")
        .toList();

    if (declarations.isEmpty()) return "";

    return declarations.stream()
        .collect(joining("\n    ", "    ", "\n\n"));
  }

  private String cfStringInitializers() {
    var initializers = parameterGenerators.stream()
        .filter(VariableGenerator::isCFString)
        .map(p -> p.cfStringName()
            + " = org.alveolo.ffm.macos.CFStringSupport.toCFString("
            + p.name() + ");")
        .toList();

    if (initializers.isEmpty()) return "";

    return initializers.stream()
        .collect(joining("\n      ", "      ", "\n\n"));
  }

  private String cfStringFinally() {
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
