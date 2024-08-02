package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignClassName;
import static org.alveolo.ffm.processor.TypeGenerator.VALUE_LAYOUT_NOT_SUPPORTED;

import java.lang.foreign.SegmentAllocator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignName;
import org.alveolo.ffm.ForeignValue;

class ExecutableGenerator {
  public static final String SEGMENT_ALLOCATOR =
      SegmentAllocator.class.getCanonicalName();

  final ProcessingEnvironment processingEnv;
  final ExecutableElement element;
  final String methodHandleName;
  final TypeGenerator returnGenerator;
  final List<VariableGenerator> parameterGenerators;

  ExecutableGenerator(ProcessingEnvironment processingEnv,
      ExecutableElement element, String methodHandleName) {
    this.processingEnv = processingEnv;
    this.element = element;
    this.methodHandleName = methodHandleName;

    returnGenerator = new TypeGenerator(processingEnv, element.getReturnType());

    parameterGenerators = element.getParameters().stream()
        .map(param -> new VariableGenerator(processingEnv, param))
        .toList();
  }

  String method() {
    return """

          private static final MethodHandle <mh> = FF$LINKER.downcallHandle(
              FF$LOOKUP.find("<name>").get(),
              <descriptor>);

          <signature> {
            try <allocator>{
              <invoke>
            } catch (RuntimeException|Error ff$e) {
              throw ff$e;
            } catch (Throwable ff$t) {
              throw new AssertionError(ff$t);
            }
          }
        """
        .replace("<mh>", methodHandleName)
        .replace("<name>", name(element))
        .replace("<descriptor>", descriptor())
        .replace("<signature>", signature())
        .replace("<allocator>", allocator())
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
        parameterGenerators.stream()
            .filter(pg -> pg.typeMirror.toString() != SEGMENT_ALLOCATOR));

    String prefix = isVoid
        ? "FunctionDescriptor.ofVoid("
        : "FunctionDescriptor.of(";

    String newLine = "\n          ";

    return stream.map(TypeGenerator::layout)
        .collect(joining("," + newLine, prefix + newLine, ")"));
  }

  private String signature() {
    String prefix = "public " + element.getReturnType()
        + " " + element.getSimpleName() + "(";

    String newLine = "\n      ";

    return parameterGenerators.stream()
        .map(VariableGenerator::signature)
        .collect(joining("," + newLine, prefix + newLine, ")"));
  }

  private String allocator() {
    return needsAllocator() ? "(var ff$arena = Arena.ofConfined()) " : "";
  }

  private String invoke() {
    var returnType = element.getReturnType();

    String newLine = "\n          ";

    String params = parameterGenerators.stream()
        .map(VariableGenerator::invoke)
        .collect(joining("," + newLine, newLine, ""));

    if (returnGenerator.needsAllocator()) {
      var type = (TypeElement) processingEnv
          .getTypeUtils().asElement(returnType);

      return "return " + foreignClassName(type)
          + ".fromMemorySegment((MemorySegment) " + methodHandleName
          + ".invokeExact(" + newLine
          + "(SegmentAllocator) ff$arena, " + params + "));";
    }

    if (returnType.getKind() != TypeKind.VOID)
      return "return (" + returnType + ") "
          + methodHandleName + ".invokeExact(" + params + ");";

    return methodHandleName + ".invokeExact(" + params + ");";
  }

  boolean needsAllocator() {
    return returnGenerator.needsAllocator()
        || parameterGenerators.stream()
            .anyMatch(VariableGenerator::needsAllocator);
  }

  /**
   * @return true if any of the method parameters has unsupported type
   */
  boolean checkParameterTypes() {
    var types = processingEnv.getTypeUtils();
    var returnType = element.getReturnType();
    var returnElement = (TypeElement) types.asElement(returnType);
    boolean hasUnsupported = false;

    boolean expectSegmentAllocator = returnElement != null
        && returnElement.getKind() == ElementKind.CLASS
        && (returnGenerator.hasAnnotation(ForeignValue.class)
            || !returnGenerator.hasAnnotation(Address.class)
                && returnElement.getAnnotation(ForeignValue.class) != null);

    for (var paramGen : parameterGenerators) {
      var typeMirror = paramGen.typeMirror;

      if (expectSegmentAllocator) {
        expectSegmentAllocator = false;

        if (!SEGMENT_ALLOCATOR.equals(typeMirror.toString())) {
          processingEnv.getMessager().printError(
              "SegmentAllocator is expected in place of " + typeMirror,
              paramGen.element);
          return true;
        }

        continue;
      }

      String layout = paramGen.layout();

      if (layout == VALUE_LAYOUT_NOT_SUPPORTED) {
        hasUnsupported = true;

        processingEnv.getMessager().printError(
            "Type is not supported: " + typeMirror, paramGen.element);
      }
    }

    return hasUnsupported;
  }
}
