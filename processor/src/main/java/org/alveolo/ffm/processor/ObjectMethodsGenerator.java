package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.qualifyName;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Virtual;

/// Analyzes and generates `@Virtual` and `@Symbol` object-method support for
/// memory-backed `@Struct` interfaces, including vtable metadata, dispatch
/// tables, symbol holders, and delegated method implementations.
final class ObjectMethodsGenerator {
  static final String VTABLE_FIELD = "vtable$F";

  record Methods(
      List<ExecutableElement> methods,
      List<ExecutableElement> virtualMethods,
      List<ExecutableElement> symbolMethods
  ) {
    Methods {
      methods = List.copyOf(methods);
      virtualMethods = List.copyOf(virtualMethods);
      symbolMethods = List.copyOf(symbolMethods);
    }

    static Methods empty() {
      return new Methods(List.of(), List.of(), List.of());
    }

    boolean hasVirtualMethods() {
      return !virtualMethods.isEmpty();
    }

    boolean hasSymbolMethods() {
      return !symbolMethods.isEmpty();
    }
  }

  record Prepared(
      Methods methods,
      List<ExecutableGenerator> symbolGenerators,
      List<ExecutableGenerator> virtualGenerators
  ) {
    Prepared {
      symbolGenerators = List.copyOf(symbolGenerators);
      virtualGenerators = List.copyOf(virtualGenerators);
    }

    boolean hasVirtualMethods() {
      return methods.hasVirtualMethods();
    }

    boolean hasUsableVirtualMethods() {
      return virtualGenerators.stream().anyMatch(generator ->
          !generator.hasErrors);
    }

    boolean hasSymbolMethods() {
      return methods.hasSymbolMethods();
    }
  }

  private final ProcessingEnvironment processingEnv;
  private final GeneratedTypeRegistry generatedTypes;

  ObjectMethodsGenerator(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes) {
    this.processingEnv = processingEnv;
    this.generatedTypes = generatedTypes;
  }

  static boolean isObjectMethod(ExecutableElement method) {
    return method.getAnnotation(Virtual.class) != null
        || method.getAnnotation(Symbol.class) != null;
  }

  Methods objectMethods(TypeElement iface) {
    var methods = new ArrayList<ExecutableElement>();
    var virtualMethods = new ArrayList<ExecutableElement>();
    var symbolMethods = new ArrayList<ExecutableElement>();

    for (var enc : iface.getEnclosedElements()) {
      if (enc instanceof ExecutableElement method
          && method.getModifiers().contains(ABSTRACT)
          && !method.getModifiers().contains(STATIC)
          && !method.getModifiers().contains(DEFAULT)) {
        if (isObjectMethod(method)) {
          methods.add(method);
        }
        if (method.getAnnotation(Virtual.class) != null) {
          virtualMethods.add(method);
        }
        if (method.getAnnotation(Symbol.class) != null) {
          symbolMethods.add(method);
        }
      }
    }

    return new Methods(methods, virtualMethods, symbolMethods);
  }

  boolean validateObjectMethods(
      TypeElement iface, Methods objectMethods, boolean vtable) {
    var valid = true;

    if (vtable) {
      for (var member : iface.getEnclosedElements()) {
        if (member.getSimpleName().contentEquals(VTABLE_FIELD)) {
          processingEnv.getMessager().printError(
              "'" + VTABLE_FIELD + "' is reserved for generated vtable access",
              member);
          valid = false;
        }
      }
    }

    if (!vtable && objectMethods.hasVirtualMethods()) {
      for (var method : objectMethods.virtualMethods()) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on @Struct(vtable = true) methods",
            method);
      }
      valid = false;
    }

    for (var method : objectMethods.methods()) {
      if (indexedFieldShape(method)) {
        processingEnv.getMessager().printError(
            "Indexed field declarations cannot be annotated @Virtual or "
                + "@Symbol",
            method);
        valid = false;
      }
    }

    var slots = new LinkedHashMap<Integer, ExecutableElement>();
    for (var method : objectMethods.virtualMethods()) {
      if (method.getAnnotation(Symbol.class) != null) {
        processingEnv.getMessager().printError(
            "@Virtual and @Symbol cannot be used on the same method", method);
        valid = false;
      }

      var slot = method.getAnnotation(Virtual.class).value();
      if (slot < 0) {
        processingEnv.getMessager().printError(
            "@Virtual value must be non-negative", method);
        valid = false;
        continue;
      }

      var previous = slots.putIfAbsent(slot, method);
      if (previous != null) {
        processingEnv.getMessager().printError(
            "Duplicate @Virtual slot: " + slot, method);
        processingEnv.getMessager().printError(
            "Duplicate @Virtual slot: " + slot, previous);
        valid = false;
      }
    }

    return valid;
  }

  Prepared prepare(TypeElement iface, Methods methods) {
    var symbolOwner = symbolOwner(iface, methods);
    if (methods.hasSymbolMethods() && symbolOwner == null) return null;

    var symbolOwnerClassName = symbolOwner == null
        ? null
        : ProcessorUtils.foreignInterfaceClassName(
            symbolOwner, processingEnv.getElementUtils());
    var symbolGenerators = symbolGenerators(methods, symbolOwnerClassName);
    var virtualGenerators = virtualGenerators(methods.virtualMethods());

    return new Prepared(methods, symbolGenerators, virtualGenerators);
  }

  void writeDispatchTable(TypeElement iface, Prepared prepared)
      throws IOException {
    if (!prepared.hasUsableVirtualMethods()) return;

    var elements = processingEnv.getElementUtils();
    var packageName = packageName(iface, elements);
    var vtableSimpleName =
        ProcessorUtils.vtableSpecificationSimpleClassName(iface);
    var vtableClassName = qualifyName(packageName, vtableSimpleName);

    var file = processingEnv.getFiler()
        .createSourceFile(vtableClassName, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          @javax.annotation.processing.Generated(
              "<generator>")
          @org.alveolo.ffm.DispatchTable
          interface <vtableName> {
          """
          .replace("<generator>",
              ForeignMemoryProcessor.class.getCanonicalName())
          .replace("<vtableName>", vtableSimpleName));

      for (var generator : prepared.virtualGenerators()) {
        if (generator.hasErrors) {
          continue;
        }

        var method = generator.element;
        out.write("""

              <firstVariadicArg>
              @org.alveolo.ffm.Slot(<slot>)
              <signature>;
            """
            .replace("<firstVariadicArg>",
                firstVariadicArgAnnotation(method))
            .replace("<slot>", Integer.toString(virtualSlot(method)))
            .replace("<signature>",
                dispatchTableMethodSignature(iface, generator)));
      }

      out.write("}\n");
    }
  }

  void writeVtableMetadata(Writer out) throws IOException {
    out.write("""

          public static final java.lang.foreign.MemoryLayout.PathElement
              vtable$F$PathElement$F =
                  java.lang.foreign.MemoryLayout.PathElement
                      .groupElement("vtable$F");

          public static final java.lang.invoke.VarHandle
              vtable$F$VarHandle$F =
              java.lang.invoke.MethodHandles.insertCoordinates(
                  MemoryLayout$F.varHandle(vtable$F$PathElement$F), 1, 0L);
        """);
  }

  void writeSymbolHolder(Writer out, Prepared prepared) throws IOException {
    if (prepared.symbolGenerators().isEmpty()) return;

    for (var generator : prepared.symbolGenerators()) {
      if (!generator.hasErrors) {
        out.write(generator.methodHandleDeclaration());
      }
    }
  }

  void writeObjectMethods(Writer out, Prepared prepared)
      throws IOException {
    var symbolIndex = 0;
    var virtualIndex = 0;
    for (var method : prepared.methods().methods()) {
      if (method.getAnnotation(Virtual.class) != null) {
        writeVirtualMethod(out,
            prepared.virtualGenerators().get(virtualIndex++));
      } else {
        var generator = prepared.symbolGenerators().get(symbolIndex++);
        out.write(generator.methodOnly(generator.methodHandleName));
      }
    }
  }

  private boolean indexedFieldShape(ExecutableElement method) {
    return method.getReturnType().getKind() != TypeKind.VOID
        && !method.getParameters().isEmpty()
        && method.getParameters().stream().allMatch(parameter -> {
          var kind = parameter.asType().getKind();
          return (kind == TypeKind.INT || kind == TypeKind.LONG)
              && TypeGenerator.hasSequence(parameter.asType(), parameter);
        });
  }

  private TypeElement symbolOwner(TypeElement iface, Methods objectMethods) {
    if (!objectMethods.hasSymbolMethods()) return null;

    var symbols = symbols(iface);
    if (symbols == null || symbols.toString().equals(Void.class.getName())) {
      processingEnv.getMessager().printError(
          "@Struct symbols is required when @Symbol methods are used", iface);
      return null;
    }

    var owner = processingEnv.getTypeUtils().asElement(symbols);
    if (!(owner instanceof TypeElement type)
        || type.getKind() != ElementKind.INTERFACE
        || type.getAnnotation(ForeignInterface.class) == null) {
      processingEnv.getMessager().printError(
          "@Struct symbols must reference an @ForeignInterface", iface);
      return null;
    }

    return type;
  }

  private TypeMirror symbols(TypeElement iface) {
    for (var mirror : iface.getAnnotationMirrors()) {
      if (!mirror.getAnnotationType().toString()
          .equals(Struct.class.getCanonicalName())) {
        continue;
      }

      for (var entry : mirror.getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals("symbols"))
          return (TypeMirror) entry.getValue().getValue();
      }
    }

    return null;
  }

  private List<ExecutableGenerator> symbolGenerators(
      Methods objectMethods, String symbolOwnerClassName) {
    var symbolMethods = objectMethods.symbolMethods();
    return IntStream.range(0, symbolMethods.size())
        .mapToObj(index -> symbolGenerator(
            symbolMethods.get(index), index, symbolOwnerClassName))
        .toList();
  }

  private List<ExecutableGenerator> virtualGenerators(
      List<ExecutableElement> methods) {
    return methods.stream()
        .map(method -> new ExecutableGenerator(
            processingEnv, generatedTypes, method,
            "UnusedMethodHandle$F", true,
            List.of(new ExecutableGenerator.NativeArgument(
                "java.lang.foreign.ValueLayout.ADDRESS", "this")),
            "", ""))
        .toList();
  }

  private ExecutableGenerator symbolGenerator(ExecutableElement method,
      int index, String symbolOwnerClassName) {
    return new ExecutableGenerator(
        processingEnv, generatedTypes, method,
        "SymbolMethodHandle$" + index + "$F", false,
        List.of(new ExecutableGenerator.NativeArgument(
            "java.lang.foreign.ValueLayout.ADDRESS",
            "this.MemorySegment$F")),
        symbolOwnerClassName + ".Linker$F",
        symbolOwnerClassName + ".SymbolLookup$F");
  }

  private void writeVirtualMethod(Writer out, ExecutableGenerator generator)
      throws IOException {
    if (generator.hasErrors) {
      out.write(generator.methodOnly(""));
      return;
    }

    var method = generator.element;
    var args = new ArrayList<String>();
    args.add("this");
    args.addAll(method.getParameters().stream()
        .map(VariableElement::getSimpleName)
        .map(Object::toString)
        .toList());
    var call = "Vtable$F()." + method.getSimpleName()
        + args.stream().collect(joining(", ", "(", ")"));

    var statement = method.getReturnType().getKind() == TypeKind.VOID
        ? call + ";"
        : "return " + call + ";";

    out.write("""

          <signature> {
            <statement>
          }
        """
        .replace("<signature>", generator.signature())
        .replace("<statement>", statement));
  }

  private String dispatchTableMethodSignature(
      TypeElement iface, ExecutableGenerator generator) {
    var method = generator.element;
    var params = new ArrayList<String>();
    params.add(iface.getSimpleName() + " self$f");
    params.addAll(generator.parameterGenerators.stream()
        .map(VariableGenerator::bridgeSignature)
        .toList());

    return generator.bridgeReturnTypeName()
        + " " + method.getSimpleName()
        + params.stream().collect(joining(",\n      ", "(\n      ", ")"));
  }

  private int virtualSlot(ExecutableElement method) {
    return method.getAnnotation(Virtual.class).value();
  }

  private String firstVariadicArgAnnotation(ExecutableElement method) {
    var annotation = method.getAnnotation(FirstVariadicArg.class);
    if (annotation == null) return "";

    return "@org.alveolo.ffm.FirstVariadicArg("
        + (annotation.value() + 1) + ")";
  }
}
