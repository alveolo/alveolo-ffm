package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignMemorySimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.qualifyName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Virtual;

@SupportedAnnotationTypes({
  "org.alveolo.ffm.Struct",
  "org.alveolo.ffm.Union",
  "org.alveolo.ffm.Virtual",
})
@SupportedSourceVersion(RELEASE_25)
public class ForeignMemoryProcessor extends AbstractProcessor {
  private static final String VTABLE_FIELD = "ff$vtbl";

  record InferredFields(
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields,
      List<ExecutableElement> unsupportedMethods
  ) {}

  record ObjectMethods(
      List<ExecutableElement> methods,
      List<ExecutableElement> virtualMethods,
      List<ExecutableElement> symbolMethods
  ) {
    static ObjectMethods empty() {
      return new ObjectMethods(List.of(), List.of(), List.of());
    }

    boolean hasVirtualMethods() {
      return !virtualMethods.isEmpty();
    }

    boolean hasSymbolMethods() {
      return !symbolMethods.isEmpty();
    }
  }

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      if (annotation.getQualifiedName().contentEquals(
          Virtual.class.getCanonicalName())) {
        validateVirtualAnnotations(roundEnv.getElementsAnnotatedWith(
            annotation));
        continue;
      }

      for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement type) {
          switch (type.getKind()) {
            case INTERFACE:
            case RECORD:
              try {
                var struct = type.getAnnotation(Struct.class);
                if (struct != null) {
                  validateSimpleClassName(annotation, struct, struct.name());
                  validateTopLevelType(type, struct);
                  if (struct.vtable()
                      && type.getKind() == ElementKind.RECORD) {
                    messager.printError(
                        "@Struct(vtable = true) can only be applied to an "
                            + "interface, not RECORD",
                        type);
                  } else {
                    writeFile(type, "struct", struct.vtable());
                  }
                }

                var union = type.getAnnotation(Union.class);
                if (union != null) {
                  validateSimpleClassName(annotation, union, union.name());
                  validateTopLevelType(type, union);
                  if (type.getKind() == ElementKind.RECORD) {
                    messager.printError("@" + annotation.getSimpleName()
                        + " can only be applied to an interface, not "
                        + ElementKind.RECORD, type);
                  } else {
                    writeFile(type, "union", false);
                  }
                }
              } catch (ProcessorError e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    e.getMessage(), e.getElement());
              } catch (Throwable e) {
                var sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                messager.printError(sw.toString(), type);
              }
              break;
            case ElementKind kind:
              messager.printError("@" + annotation.getSimpleName()
                  + " can only be applied to an interface, not " + kind, type);
          }
        }
      }
    }

    return true;
  }

  private void validateVirtualAnnotations(Set<? extends Element> elements) {
    for (var element : elements) {
      if (!(element instanceof ExecutableElement method)
          || !(method.getEnclosingElement() instanceof TypeElement owner)) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on methods of @Struct(vtable = true)",
            element);
        continue;
      }

      var struct = owner.getAnnotation(Struct.class);
      if (struct == null || !struct.vtable()) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on @Struct(vtable = true) methods",
            method);
        continue;
      }

      if (method.getKind() != ElementKind.METHOD
          || !method.getModifiers().contains(ABSTRACT)
          || method.getModifiers().contains(STATIC)
          || method.getModifiers().contains(DEFAULT)) {
        processingEnv.getMessager().printError(
            "@Virtual is only allowed on abstract instance methods",
            method);
      }
    }
  }

  /// Infer struct fields from interface accessor methods.
  private InferredFields inferFields(
      TypeElement iface, boolean excludeObjectMethods) {
    if (iface.getKind() == ElementKind.RECORD)
      return inferRecordFields(iface);

    // Group methods by name
    Map<String, List<ExecutableElement>> methodsByName = new LinkedHashMap<>();
    for (var enc : iface.getEnclosedElements()) {
      if (enc instanceof ExecutableElement exe
          && enc.getModifiers().contains(ABSTRACT)
          && !enc.getModifiers().contains(STATIC)
          && !enc.getModifiers().contains(DEFAULT)) {
        if (excludeObjectMethods && isObjectMethod(exe)) {
          continue;
        }
        String name = exe.getSimpleName().toString();
        methodsByName.computeIfAbsent(name, _ -> new ArrayList<>()).add(exe);
      }
    }

    var fields = new ArrayList<VariableGenerator>();
    var indexedFields = new LinkedHashMap<String, IndexedField>();
    var unsupportedMethods = new ArrayList<ExecutableElement>();
    for (var entry : methodsByName.entrySet()) {
      String fieldName = entry.getKey();
      var methods = entry.getValue();

      ExecutableElement accessor = null;
      var indexedCandidates = new ArrayList<ExecutableElement>();
      var fieldMethods = new ArrayList<ExecutableElement>();

      for (var m : methods) {
        var params = m.getParameters();
        if (params.isEmpty() && m.getReturnType().getKind() != TypeKind.VOID) {
          accessor = m;
        } else if (!params.isEmpty()
            && m.getReturnType().getKind() != TypeKind.VOID) {
          indexedCandidates.add(m);
        } else {
          fieldMethods.add(m);
        }
      }

      if (accessor == null && indexedCandidates.size() == 1
          && fieldMethods.isEmpty()) {
        var indexedMethod = indexedCandidates.getFirst();
        var indexed = indexedInterfaceField(indexedMethod);
        if (indexed != null) {
          var collision = indexedHelperCollision(
              methodsByName.keySet(), indexed);
          if (collision == null) {
            fields.add(indexed.element());
            indexedFields.put(fieldName, indexed);
          } else {
            reportIndexedHelperCollision(indexed, collision);
            addUnsupportedMethod(unsupportedMethods, indexedMethod);
          }
        } else {
          addUnsupportedMethod(unsupportedMethods, indexedMethod);
        }
        continue;
      }

      if (accessor == null) {
        if (indexedCandidates.size() > 1) {
          for (var candidate : indexedCandidates) {
            processingEnv.getMessager().printError(
                "Field '" + fieldName
                    + "' has multiple indexed accessor declarations",
                candidate);
          }
        } else {
          processingEnv.getMessager().printError(
              "Field '" + fieldName + "' has no accessor",
              methods.get(0));
        }
        for (var m : methods) {
          addUnsupportedMethod(unsupportedMethods, m);
        }
        continue;
      }

      // Determine type from accessor return.
      var fieldType = accessor.getReturnType();
      if (fieldType.getKind() == TypeKind.ARRAY) {
        processingEnv.getMessager().printError(
            "Array-returning interface fields are not supported; declare an "
                + "indexed element accessor whose int or long parameters "
                + "carry @Sequence",
            accessor);
        for (var m : methods) {
          addUnsupportedMethod(unsupportedMethods, m);
        }
        continue;
      }

      var accessorGenerator = new VariableGenerator(processingEnv, fieldName,
          fieldType, TypeGenerator.sequence(fieldType, accessor), accessor);
      var bufferField = accessorGenerator.isNioBuffer();
      if (bufferField) {
        processingEnv.getMessager().printError(
            "NIO Buffer types are not supported as @Struct or @Union fields; "
                + "declare an indexed element accessor, for example 'int "
                + fieldName + "(@Sequence(N) long index)'",
            accessor);
      }

      for (var method : indexedCandidates) {
        fieldMethods.add(method);
      }
      for (var method : fieldMethods) {
        if (!bufferField) {
          processingEnv.getMessager().printError(
              "Unsupported accessor signature for field '"
                  + fieldName + "': " + method,
              method);
        }
        if (bufferField) {
          addUnsupportedMethod(unsupportedMethods, method);
        }
      }

      fields.add(accessorGenerator);
    }

    return new InferredFields(fields, indexedFields, unsupportedMethods);
  }

  private InferredFields inferRecordFields(TypeElement record) {
    var fields = new ArrayList<VariableGenerator>();
    var indexedFields = new LinkedHashMap<String, IndexedField>();

    var componentNames = record.getRecordComponents().stream()
        .map(component -> component.getSimpleName().toString())
        .collect(java.util.stream.Collectors.toSet());
    for (var component : record.getRecordComponents()) {
      if (component.asType().getKind() != TypeKind.ARRAY) {
        fields.add(new VariableGenerator(processingEnv, component));
        continue;
      }

      var indexed = indexedRecordField(component);
      if (indexed == null) {
        // Keep a generated, throwing converter after reporting the focused
        // diagnostic.  This avoids secondary "generated class not found"
        // errors in clients that compile alongside the invalid declaration.
        fields.add(new VariableGenerator(processingEnv, component));
        continue;
      }

      var collision = indexedHelperCollision(componentNames, indexed);
      if (collision != null) {
        reportIndexedHelperCollision(indexed, collision);
        fields.add(new VariableGenerator(processingEnv, component));
        continue;
      }

      fields.add(indexed.element());
      indexedFields.put(indexed.name(), indexed);
    }

    return new InferredFields(fields, indexedFields, List.of());
  }

  private String indexedHelperCollision(
      Set<String> declaredNames, IndexedField indexed) {
    var generatedNames = new ArrayList<String>();
    generatedNames.add(indexed.name() + "$MemorySegment");
    if (indexed.addressElement())
      generatedNames.add(indexed.name() + "$Address");
    if (indexed.oneDimensional() && indexed.primitive()) {
      generatedNames.add(indexed.name() + "$Buffer");
      generatedNames.add(indexed.name() + "$Array");
    } else if (indexed.recordSnapshot()) {
      generatedNames.add(indexed.name() + "$Array");
    }

    return generatedNames.stream()
        .filter(declaredNames::contains)
        .findFirst().orElse(null);
  }

  private void reportIndexedHelperCollision(
      IndexedField indexed, String helperName) {
    processingEnv.getMessager().printError(
        "Generated indexed field helper '" + helperName
            + "' collides with a declared field",
        indexed.declaration());
  }

  private IndexedField indexedInterfaceField(ExecutableElement accessor) {
    var valid = true;
    var dimensions = new ArrayList<IndexedField.Dimension>();
    for (var parameter : accessor.getParameters()) {
      var kind = parameter.asType().getKind();
      if (kind != TypeKind.INT && kind != TypeKind.LONG) {
        processingEnv.getMessager().printError(
            "Indexed field parameters must be int or long", parameter);
        valid = false;
      }

      if (!TypeGenerator.hasSequence(parameter.asType(), parameter)) {
        processingEnv.getMessager().printError(
            "Each indexed field parameter must carry @Sequence", parameter);
        valid = false;
        continue;
      }

      var size = TypeGenerator.sequence(parameter.asType(), parameter);
      if (size <= 0) {
        processingEnv.getMessager().printError(
            "Indexed field @Sequence value must be positive", parameter);
        valid = false;
      }
      if (parameter.asType().getKind() == TypeKind.INT
          && size > Integer.MAX_VALUE) {
        processingEnv.getMessager().printError(
            "An int indexed field parameter cannot address a dimension "
                + "larger than Integer.MAX_VALUE",
            parameter);
        valid = false;
      }
      dimensions.add(new IndexedField.Dimension(
          parameter.getSimpleName().toString(),
          kind == TypeKind.INT ? "int" : "long",
          size));
    }

    var type = accessor.getReturnType();
    var name = accessor.getSimpleName().toString();
    var element = new VariableGenerator(
        processingEnv, name, type, 1L, accessor);

    if (TypeGenerator.hasSequence(type, accessor)) {
      processingEnv.getMessager().printError(
          "Place @Sequence on each indexed field parameter, not on the "
              + "accessor return",
          accessor);
      valid = false;
    }

    if (type.getKind() == TypeKind.ARRAY || element.isNioBuffer()) {
      processingEnv.getMessager().printError(
          "Indexed fields must return one element, not an array or Buffer",
          accessor);
      valid = false;
    }

    if (!validIndexedElement(element, false)) valid = false;
    if (dimensions.size() == 1 && element.isPrimitive()
        && dimensions.getFirst().size() > Integer.MAX_VALUE) {
      processingEnv.getMessager().printError(
          "One-dimensional primitive indexed fields cannot exceed "
              + "Integer.MAX_VALUE because Java array and Buffer views use "
              + "int sizes",
          accessor);
      valid = false;
    }
    if (!valid) return null;

    return new IndexedField(element, type, dimensions, accessor, false);
  }

  private IndexedField indexedRecordField(RecordComponentElement component) {
    var arrayType = (ArrayType) component.asType();
    var componentType = arrayType.getComponentType();
    var valid = true;

    if (componentType.getKind() == TypeKind.ARRAY) {
      processingEnv.getMessager().printError(
          "Multidimensional Java array record fields are not supported",
          component);
      valid = false;
    }

    if (!TypeGenerator.hasSequence(component.asType(), component)) {
      processingEnv.getMessager().printError(
          "Record array fields must carry one positive @Sequence",
          component);
      valid = false;
    }

    var size = TypeGenerator.sequence(component.asType(), component);
    if (size <= 0) {
      processingEnv.getMessager().printError(
          "Record array field @Sequence value must be positive", component);
      valid = false;
    }
    if (size > Integer.MAX_VALUE) {
      processingEnv.getMessager().printError(
          "Record array field @Sequence value cannot exceed "
              + "Integer.MAX_VALUE",
          component);
      valid = false;
    }

    var element = new VariableGenerator(processingEnv,
        component.getSimpleName().toString(), componentType, 1L, component);
    if (!validIndexedElement(element, true)) valid = false;
    if (!valid) return null;

    var dimension = new IndexedField.Dimension(
        "index", "long", size);
    return new IndexedField(element, component.asType(), List.of(dimension),
        component, true);
  }

  private boolean validIndexedElement(
      VariableGenerator element, boolean recordSnapshot) {
    if (element.isPrimitiveAddress()) {
      processingEnv.getMessager().printError(
          "@Address primitive array elements are not supported",
          element.element);
      return false;
    }

    if (element.isPrimitive()) return true;

    if (recordSnapshot) {
      if (!element.isForeignMemory()
          || !element.isRecord()
          || !element.isValue()) {
        processingEnv.getMessager().printError(
            "Record arrays support primitives and value-style @Struct "
                + "record elements only",
            element.element);
        return false;
      }
      return true;
    }

    if (element.isMemorySegment()) return true;
    if (element.isForeignMemory()
        && (element.isValue() || element.isAddress())) return true;

    processingEnv.getMessager().printError(
        "Indexed fields support primitives, MemorySegment, and @Struct or "
            + "@Union elements",
        element.element);
    return false;
  }

  private void addUnsupportedMethod(
      List<ExecutableElement> unsupportedMethods, ExecutableElement method) {
    if (!unsupportedMethods.contains(method)) {
      unsupportedMethods.add(method);
    }
  }

  private boolean isObjectMethod(ExecutableElement method) {
    return method.getAnnotation(Virtual.class) != null
        || method.getAnnotation(Symbol.class) != null;
  }

  private ObjectMethods objectMethods(TypeElement iface) {
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

    return new ObjectMethods(methods, virtualMethods, symbolMethods);
  }

  private boolean validateObjectMethods(
      TypeElement iface, ObjectMethods objectMethods, boolean vtable) {
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

  private boolean indexedFieldShape(ExecutableElement method) {
    return method.getReturnType().getKind() != TypeKind.VOID
        && !method.getParameters().isEmpty()
        && method.getParameters().stream().allMatch(parameter -> {
          var kind = parameter.asType().getKind();
          return (kind == TypeKind.INT || kind == TypeKind.LONG)
              && TypeGenerator.hasSequence(parameter.asType(), parameter);
        });
  }

  private TypeElement symbolOwner(
      TypeElement iface, ObjectMethods objectMethods) {
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

  /// Buffer snapshots are intentionally not part of the record model.  Array
  /// snapshots are validated while inferring their element and dimensions.
  private boolean validateRecordComponents(TypeElement type) {
    if (type.getKind() != ElementKind.RECORD) return true;

    var valid = true;
    for (var component : type.getRecordComponents()) {
      var componentType = component.asType();
      if (componentType.getKind() == TypeKind.ARRAY) {
        continue;
      }
      if (new TypeGenerator(processingEnv, componentType).isNioBuffer()) {
        processingEnv.getMessager().printError(
            "NIO Buffer types are not supported as record components; "
                + "use a one-dimensional array component annotated "
                + "@Sequence",
            component);
        valid = false;
      }
    }

    return valid;
  }

  private void writeFile(TypeElement iface, String kind, boolean vtable)
      throws IOException {
    validateRecordComponents(iface);

    var elements = processingEnv.getElementUtils();
    String packageName = packageName(iface, elements);
    String ifaceSimpleName = iface.getSimpleName().toString();
    String className = foreignMemoryClassName(iface);
    String simpleClassName = foreignMemorySimpleClassName(iface);
    String vtableSimpleName = ifaceSimpleName + "Vtbl";

    var isStructInterface = kind.equals("struct")
        && iface.getKind() == ElementKind.INTERFACE;
    var objectMethods = isStructInterface
        ? objectMethods(iface)
        : ObjectMethods.empty();
    if (!validateObjectMethods(iface, objectMethods, vtable)) return;

    var symbolOwner = symbolOwner(iface, objectMethods);
    if (objectMethods.hasSymbolMethods() && symbolOwner == null) return;
    var symbolOwnerClassName = symbolOwner == null
        ? null
        : foreignInterfaceClassName(symbolOwner);
    var symbolGenerators = symbolGenerators(objectMethods,
        symbolOwnerClassName);
    var virtualGenerators = virtualGenerators(objectMethods.virtualMethods());

    if (objectMethods.hasVirtualMethods()) {
      writeDispatchTable(iface, packageName, ifaceSimpleName,
          vtableSimpleName, virtualGenerators);
    }

    var structFields = inferFields(iface, isStructInterface);
    var fields = structFields.fields();
    var indexedFields = structFields.indexedFields();
    validateFields(fields, indexedFields);

    var file = processingEnv.getFiler().createSourceFile(className, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
        out.append("package ").append(packageName).append(";\n\n");
      }

      String declaration = switch (iface.getKind()) {
        case INTERFACE -> simpleClassName + " implements "
            + ifaceSimpleName;
        case RECORD -> simpleClassName;
        case ElementKind k1 -> throw new IllegalArgumentException(
            "Unexpected value: " + k1);
      };

      out.write("""
          import java.lang.foreign.*;
          <methodHandleImport>
          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <declaration> {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<declaration>", declaration)
          .replace("<methodHandleImport>", objectMethods.hasSymbolMethods()
              ? "import java.lang.invoke.MethodHandle;\n" : ""));

      writeLayout(out, fields, indexedFields, kind, vtable);
      writePathElements(out, simpleClassName, fields, indexedFields, vtable);
      writeVarHandles(out, simpleClassName, fields, indexedFields, vtable);
      writeAllocators(out);
      writeReinterprets(out, iface, simpleClassName);
      writeArrayElementHelpers(out, iface, simpleClassName);
      switch (iface.getKind()) {
        case INTERFACE:
          writeConstructors(out, simpleClassName, vtableSimpleName,
              objectMethods.hasVirtualMethods());
          writeFieldAccessors(out, simpleClassName, fields, indexedFields);
          writeUnsupportedMethods(out, structFields.unsupportedMethods());
          writeSymbolHolder(out, symbolGenerators);
          writeObjectMethods(out, objectMethods, symbolGenerators,
              virtualGenerators);
          break;
        case RECORD:
          writeRecordConverters(
              out, ifaceSimpleName, iface, fields, indexedFields);
          writeStaticAccessors(out, fields, indexedFields);
          break;
        case ElementKind k:
          throw new IllegalArgumentException("Unexpected value: " + k);
      }

      out.write("}\n");
    }
  }

  private void writeDispatchTable(TypeElement iface, String packageName,
      String ifaceSimpleName, String vtableSimpleName,
      List<ExecutableGenerator> generators) throws IOException {
    String vtableClassName = qualifyName(packageName, vtableSimpleName);

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
          interface <name>Vtbl {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", ifaceSimpleName));

      for (var generator : generators) {
        if (generator.hasErrors) {
          continue;
        }

        var method = generator.element;
        out.write("""

              @org.alveolo.ffm.Slot(<slot>)
              <signature>;
            """
            .replace("<slot>", Integer.toString(virtualSlot(method)))
            .replace("<signature>",
                dispatchTableMethodSignature(iface, method)));
      }

      out.write("}\n");
    }
  }

  private String dispatchTableMethodSignature(TypeElement iface,
      ExecutableElement method) {
    var params = new ArrayList<String>();
    params.add(iface.getSimpleName() + " ff$self");
    params.addAll(method.getParameters().stream()
        .map(this::sourceParameter)
        .toList());

    return sourceReturnType(method)
        + " " + method.getSimpleName()
        + params.stream().collect(joining(",\n      ", "(\n      ", ")"));
  }

  private List<ExecutableGenerator> symbolGenerators(
      ObjectMethods objectMethods, String symbolOwnerClassName) {
    var generators = new ArrayList<ExecutableGenerator>();
    var index = 0;
    for (var method : objectMethods.symbolMethods()) {
      generators.add(symbolGenerator(method, index++, symbolOwnerClassName));
    }
    return generators;
  }

  private List<ExecutableGenerator> virtualGenerators(
      List<ExecutableElement> methods) {
    return methods.stream()
        .map(method -> new ExecutableGenerator(
            processingEnv, method, "FF$VH$unused", true))
        .toList();
  }

  private void writeSymbolHolder(
      Writer out, List<ExecutableGenerator> symbolGenerators)
      throws IOException {
    if (symbolGenerators.isEmpty()) return;

    out.write("""

          private static final class FF$SYMBOLS {
        """);

    for (var generator : symbolGenerators) {
      if (!generator.hasErrors) {
        out.write(generator.methodHandleDeclaration()
            .replace("\n  ", "\n    "));
      }
    }

    out.write("""
          }
        """);
  }

  private void writeObjectMethods(Writer out, ObjectMethods objectMethods,
      List<ExecutableGenerator> symbolGenerators,
      List<ExecutableGenerator> virtualGenerators)
      throws IOException {
    var symbolIndex = 0;
    var virtualIndex = 0;
    for (var method : objectMethods.methods()) {
      if (method.getAnnotation(Virtual.class) != null) {
        writeVirtualMethod(out, virtualGenerators.get(virtualIndex++));
      } else {
        var generator = symbolGenerators.get(symbolIndex++);
        out.write(generator.methodOnly(
            "FF$SYMBOLS." + generator.methodHandleName));
      }
    }
  }

  private ExecutableGenerator symbolGenerator(ExecutableElement method,
      int index, String symbolOwnerClassName) {
    return new ExecutableGenerator(
        processingEnv, method, "FF$MH$" + index, false,
        List.of(new ExecutableGenerator.NativeArgument(
            "ValueLayout.ADDRESS", "this.ms")),
        symbolOwnerClassName + ".FF$LINKER",
        symbolOwnerClassName + ".FF$LOOKUP");
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
    String call = "ff$vtbl()." + method.getSimpleName()
        + args.stream().collect(joining(", ", "(", ")"));

    String statement = method.getReturnType().getKind() == TypeKind.VOID
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

  private String sourceReturnType(ExecutableElement method) {
    return method.getReturnType().toString();
  }

  private String sourceMethodSignature(ExecutableElement method) {
    return "public " + sourceReturnType(method) + " "
        + method.getSimpleName()
        + method.getParameters().stream()
            .map(this::sourceParameter)
            .collect(joining(",\n      ", "(\n      ", ")"));
  }

  private String sourceParameter(VariableElement parameter) {
    return parameter.asType() + " " + parameter.getSimpleName();
  }

  private String foreignInterfaceClassName(TypeElement type) {
    return ProcessorUtils.foreignInterfaceClassName(
        type, processingEnv.getElementUtils());
  }

  private String foreignMemoryClassName(TypeElement type) {
    return ProcessorUtils.foreignMemoryClassName(
        type, processingEnv.getElementUtils());
  }

  private int virtualSlot(ExecutableElement method) {
    return method.getAnnotation(Virtual.class).value();
  }

  private void writeLayout(Writer out, List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields, String kind, boolean vtable)
      throws IOException {
    out.write("""
          public static final MemoryLayout FM$LAYOUT =
              MemoryLayout.<kind>Layout(
                  org.alveolo.ffm.ForeignUtils.<kind>Pad(new MemoryLayout [] {
        """
        .replace("<kind>", kind));

    if (vtable) {
      out.write("        ValueLayout.ADDRESS.withName(\""
          + VTABLE_FIELD + "\"),\n");
    }

    var layoutFields = fields.stream()
        .map(f -> {
          var indexed = indexedFields.get(f.name());
          return new MemoryLayoutGenerator.LayoutField(f.name(),
              indexed == null ? f.layout() : indexed.layout(),
              f.unsupported(), f.typeName(), f.element);
        })
        .toList();

    var layoutGen = new MemoryLayoutGenerator(processingEnv, layoutFields);
    out.write(layoutGen.layout());

    out.write("      }));\n");
  }

  private void writeAllocators(Writer out) throws IOException {
    out.write("""

          public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(
              FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
          }

          public static MemorySegment allocate(
              SegmentAllocator allocator, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return allocator.allocate(FM$LAYOUT, count);
          }
        """);
  }

  private void writeReinterprets(
      Writer out, TypeElement sourceType, String className)
      throws IOException {
    var isRecord = sourceType.getKind() == ElementKind.RECORD;
    var returnType = isRecord
        ? sourceType.getSimpleName().toString() : className;
    var expression = isRecord
        ? "fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()))"
        : "new " + className
            + "(ms.reinterpret(FM$LAYOUT.byteSize()))";

    out.write("""

          public static <type> reinterpret(MemorySegment ms) {
            return <expression>;
          }

          public static MemorySegment reinterpret(
              MemorySegment ms, long count) {
            if (count < 0) {
              throw new IllegalArgumentException("count must be non-negative");
            }
            return ms.reinterpret(Math.multiplyExact(
                FM$LAYOUT.byteSize(), count));
          }
        """
        .replace("<type>", returnType)
        .replace("<expression>", expression));
  }

  private void writeArrayElementHelpers(
      Writer out, TypeElement sourceType, String className)
      throws IOException {
    out.write("""

          private static MemorySegment FM$at(MemorySegment array, long index) {
            if (index < 0) {
              throw new IndexOutOfBoundsException(index);
            }
            return array.asSlice(Math.multiplyExact(
                index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
          }
        """);

    if (sourceType.getKind() == ElementKind.RECORD) {
      var sourceName = sourceType.getSimpleName().toString();
      out.write("""

          public static <source> at(MemorySegment array, long index) {
            return fromMemorySegment(FM$at(array, index));
          }
        """
        .replace("<source>", sourceName));
      return;
    }

    out.write("""

          public static <class> at(MemorySegment array, long index) {
            return new <class>(FM$at(array, index));
          }
        """
        .replace("<class>", className));
  }

  private void writeRecordConverters(Writer out, String srcClassName,
      TypeElement type, List<VariableGenerator> rcGens,
      Map<String, IndexedField> indexedFields) throws IOException {

    boolean needsAllocator = rcGens.stream()
        .anyMatch(this::needsAllocatorWrite);

    String fromMemorySegmentFields = rcGens.stream()
        .map(gen -> gen.name() + "(ms)")
        .collect(joining(",\n        "));

    var toMemorySegmentFields = recordFieldWrites(
        rcGens, indexedFields, needsAllocator)
        .indent(2);

    var toMemorySegmentMethod = (needsAllocator
        ? """
            public static void toMemorySegment(
                <src> from, MemorySegment ms, SegmentAllocator ff$allocator) {
              <toMemorySegmentFields>
            }
            """
        : """
            public static void toMemorySegment(<src> from, MemorySegment ms) {
              <toMemorySegmentFields>
            }
            """)
        .replace("<src>", srcClassName)
        .replace("  <toMemorySegmentFields>\n", toMemorySegmentFields)
        .indent(2)
        .stripTrailing();

    out.write("""

        <toMemorySegmentMethod>

          public static MemorySegment toMemorySegment(
              SegmentAllocator allocator, <src> from) {
            var ms = allocate(allocator);
            <toMemorySegment>
            return ms;
          }

          public static <src> fromMemorySegment(MemorySegment ms) {
            return new <src>(
                <fromMemorySegmentFields>);
          }
        """
        .replace("<toMemorySegmentMethod>", toMemorySegmentMethod)
        .replace("<src>", srcClassName)
        .replace("<toMemorySegment>", needsAllocator
            ? "toMemorySegment(from, ms, allocator);"
            : "toMemorySegment(from, ms);")
        .replace("<fromMemorySegmentFields>", fromMemorySegmentFields));
  }

  private String recordFieldWrites(
      List<VariableGenerator> variables,
      Map<String, IndexedField> indexedFields, boolean withAllocator) {
    return variables.stream()
        .map(v -> recordFieldWrite(
            v, indexedFields.get(v.name()), withAllocator))
        .collect(joining("\n"));
  }

  private String recordFieldWrite(
      VariableGenerator variable, IndexedField indexed,
      boolean withAllocator) {
    var name = variable.name();
    var needsAllocator = withAllocator && (indexed == null
        ? needsAllocatorWrite(variable)
        : indexedArrayNeedsAllocator(indexed));

    return """
        <name>(ms, <allocator>from.<name>());
        """
        .replace("<name>", name)
        .replace("<allocator>", needsAllocator ? "ff$allocator, " : "")
        .strip();
  }

  private void writeConstructors(Writer out,
      String className, String vtableTypeName, boolean hasVirtualMethods)
      throws IOException {
    out.write("""

          public final MemorySegment ms;
        """);

    if (hasVirtualMethods) {
      out.write("""

            private final <iface>Vtbl ff$vtbl;
          """
          .replace("<iface>Vtbl", vtableTypeName));
    }

    out.write("""

          public <class>(SegmentAllocator allocator) {
            this(allocate(allocator));
          }

          public <class>(MemorySegment ms) {
            this.ms = ms;
        """
        .replace("<class>", className));

    if (hasVirtualMethods) {
      out.write("    this.ff$vtbl = " + vtableTypeName
          + "FD.reinterpret((MemorySegment) FM$VH$ff$vtbl.get(ms));\n");
    }

    out.write("""
          }
        """);

    if (hasVirtualMethods) {
      out.write("""

            private <iface>Vtbl ff$vtbl() {
              return ff$vtbl;
            }
          """
          .replace("<iface>Vtbl", vtableTypeName));
    }
  }

  private void writePathElements(Writer out, String className,
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields, boolean vtable)
      throws IOException {
    if (vtable) {
      out.write("""

            public static final MemoryLayout.PathElement FM$PE$ff$vtbl =
                MemoryLayout.PathElement.groupElement("ff$vtbl");
          """);
    }

    for (var field : fields) {
      out.write("""

            public static final MemoryLayout.PathElement FM$PE$<name> =
                MemoryLayout.PathElement.groupElement("<name>");
          """
          .replace("<name>", field.name()));

      var indexed = indexedFields.get(field.name());
      if (indexed == null) continue;

      for (var i = 0; i < indexed.dimensions().size(); i++) {
        out.write("""

            public static final MemoryLayout.PathElement FM$PE$<name>$<index> =
                MemoryLayout.PathElement.sequenceElement();
          """
          .replace("<name>", field.name())
          .replace("<index>", Integer.toString(i)));
      }

      out.write("""

          public static final MemoryLayout FM$LAYOUT$<name> =
              FM$LAYOUT.select(FM$PE$<name>);

          public static final MemoryLayout FM$ELEMENT_LAYOUT$<name> =
              <elementLayout>;

          public static final long FM$OFFSET$<name> =
              FM$LAYOUT.byteOffset(FM$PE$<name>);

          public static final long FM$SIZE$<name> =
              FM$LAYOUT$<name>.byteSize();
        """
        .replace("<name>", field.name())
        .replace("<elementLayout>", indexed.elementLayout()));

      for (var i = 0; i < indexed.dimensions().size(); i++) {
        out.write("""

            public static final long FM$DIMENSION$<name>$<index> = <size>L;
          """
          .replace("<name>", field.name())
          .replace("<index>", Integer.toString(i))
          .replace("<size>", Long.toString(
              indexed.dimensions().get(i).size())));
      }
    }
  }

  private void writeVarHandles(Writer out, String className,
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields, boolean vtable)
      throws IOException {
    if (vtable) {
      out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$ff$vtbl =
                java.lang.invoke.MethodHandles.insertCoordinates(
                    FM$LAYOUT.varHandle(FM$PE$ff$vtbl), 1, 0L);
          """);
    }

    for (var field : fields) {
      var indexed = indexedFields.get(field.name());
      if (indexed != null) {
        if (indexed.structuredValueElement()) continue;

        var paths = new ArrayList<String>();
        paths.add("FM$PE$" + field.name());
        for (var i = 0; i < indexed.dimensions().size(); i++) {
          paths.add("FM$PE$" + field.name() + "$" + i);
        }

        out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$<name> =
                java.lang.invoke.MethodHandles.insertCoordinates(
                    FM$LAYOUT.varHandle(<paths>), 1, 0L);
          """
          .replace("<name>", field.name())
          .replace("<paths>", String.join(", ", paths)));
        continue;
      }

      if (field.isNioBuffer()
          || (field.isForeignMemory() && field.isValue())) {
        continue; // TODO complex/structured/indexed accessors
      }

      var initializer = field.sequence > 1
          ? "FM$LAYOUT.varHandle(FM$PE$<name>);"
          : """
              java.lang.invoke.MethodHandles.insertCoordinates(
                  FM$LAYOUT.varHandle(FM$PE$<name>), 1, 0L);
              """.stripTrailing();

      out.write("""

            public static final java.lang.invoke.VarHandle FM$VH$<name> =
                <initializer>
          """
          .replace("<initializer>",
              initializer.replace("\n", "\n      "))
          .replace("<name>", field.name()));
    }
  }

  /// Receiver style for generated field accessors: records use static accessors
  /// over an explicit MemorySegment parameter, memory-backed interfaces use
  /// instance accessors over `this.ms` with fluent setters.
  private record AccessorTarget(boolean isStatic, String className) {
    static final AccessorTarget STATIC = new AccessorTarget(true, null);

    static AccessorTarget fluent(String className) {
      return new AccessorTarget(false, className);
    }

    String getterHead(VariableGenerator field) {
      return (isStatic
          ? "public static <type> <name>(MemorySegment ms)"
          : "public <type> <name>()")
              .replace("<type>", field.typeName())
              .replace("<name>", field.name());
    }

    String setterHead(VariableGenerator field, boolean needsAllocator) {
      var head = isStatic
          ? staticSetterHead(needsAllocator)
          : fluentSetterHead(needsAllocator).replace("<class>", className);

      return head
          .replace("<type>", field.typeName())
          .replace("<name>", field.name());
    }

    private String staticSetterHead(boolean needsAllocator) {
      return needsAllocator
          ? "public static void <name>(\n"
              + "      MemorySegment ms, SegmentAllocator allocator,"
              + " <type> value)"
          : "public static void <name>(MemorySegment ms, <type> value)";
    }

    private String fluentSetterHead(boolean needsAllocator) {
      return needsAllocator
          ? "public <class> <name>(\n"
              + "      SegmentAllocator allocator, <type> value)"
          : "public <class> <name>(<type> value)";
    }
  }

  private void writeStaticAccessors(Writer out,
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields) throws IOException {
    for (var field : fields) {
      var indexed = indexedFields.get(field.name());
      if (indexed == null) {
        writeAccessorsSimple(out, AccessorTarget.STATIC, field);
      } else {
        writeIndexedStaticAccessors(out, indexed);
      }
    }
  }

  private void writeFieldAccessors(Writer out, String className,
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields) throws IOException {
    var target = AccessorTarget.fluent(className);
    for (var field : fields) {
      var indexed = indexedFields.get(field.name());
      if (indexed != null) {
        writeIndexedFieldAccessors(out, className, indexed);
      } else if (field.isNioBuffer()) {
        writeThrowingFieldAccessors(out, className, field, false);
      } else {
        writeAccessorsSimple(out, target, field);
      }
    }
  }

  private void writeIndexedFieldAccessors(
      Writer out, String className, IndexedField indexed)
      throws IOException {
    writeIndexedSegmentHelpers(out, indexed, false);
    if (indexed.addressElement()) {
      writeIndexedAddressHelpers(out, className, indexed);
    }

    var field = indexed.element();
    var name = indexed.name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var needsAllocator = indexedElementNeedsAllocator(indexed);
    var valueName = unusedIndexedParameterName(indexed, "value");
    var allocatorName = unusedIndexedParameterName(indexed, "allocator");
    var allocator = needsAllocator
        ? "SegmentAllocator " + allocatorName + ", " : "";

    if (indexed.addressElement() && !field.isMemorySegment()) {
      var foreignClass = foreignMemoryClassName(field.typeElement);
      out.write("""

          public <type> <name>(<params>) {
            var address = <name>$Address(<args>);
            return address.address() == 0L
                ? null
                : <foreignClass>.reinterpret(address);
          }
        """
        .replace("<type>", field.typeName())
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<args>", args)
        .replace("<foreignClass>", foreignClass));
    } else {
      out.write("""

          public <type> <name>(<params>) {
            return <expression>;
          }
        """
        .replace("<type>", field.typeName())
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<expression>", indexedGetterExpression(
            indexed, "ms", args, false)));
    }

    out.write("""

          public <class> <name>(
              <allocator><params>,
              <type> <valueName>) {
            <body>
            return this;
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<allocator>", allocator)
        .replace("<params>", params)
        .replace("<type>", field.typeName())
        .replace("<valueName>", valueName)
        .replace("<body>", indexedSetterBody(
            indexed, "ms", args, false, needsAllocator)));

    if (indexed.oneDimensional() && indexed.primitive()) {
      writeIndexedPrimitiveConveniences(out, className, indexed, false);
    }
  }

  private void writeIndexedStaticAccessors(
      Writer out, IndexedField indexed) throws IOException {
    writeIndexedSegmentHelpers(out, indexed, true);

    var field = indexed.element();
    var name = indexed.name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var needsAllocator = indexedElementNeedsAllocator(indexed);
    var valueName = unusedIndexedParameterName(indexed, "value");
    var allocatorName = unusedIndexedParameterName(indexed, "allocator");
    var allocator = needsAllocator
        ? "SegmentAllocator " + allocatorName + ", " : "";

    out.write("""

          public static <type> <name>(
              MemorySegment ms, <params>) {
            return <expression>;
          }

          public static void <name>(MemorySegment ms, <allocator><params>,
              <type> <valueName>) {
            <body>
          }
        """
        .replace("<type>", field.typeName())
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<allocator>", allocator)
        .replace("<valueName>", valueName)
        .replace("<expression>", indexedGetterExpression(
            indexed, "ms", args, true))
        .replace("<body>", indexedSetterBody(
            indexed, "ms", args, true, needsAllocator)));

    if (indexed.oneDimensional() && indexed.primitive()) {
      writeIndexedPrimitiveConveniences(out, null, indexed, true);
    } else if (indexed.recordSnapshot()) {
      writeIndexedRecordArrayConveniences(out, indexed, needsAllocator);
    }
  }

  private void writeIndexedSegmentHelpers(
      Writer out, IndexedField indexed, boolean isStatic)
      throws IOException {
    var name = indexed.name();
    var params = indexed.parameterDeclarations();
    var receiver = isStatic ? "MemorySegment ms" : "";
    var receiverAndParams = isStatic
        ? receiver + indexed.commaPrefixedParameterDeclarations()
        : params;
    var offsetArgument = (indexedLeafOffset(indexed) + ",")
        .replace("\n", "\n      ");

    out.write((isStatic ? """

        public static MemorySegment <name>$MemorySegment(MemorySegment ms) {
          return ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
        }
        """ : """

        public MemorySegment <name>$MemorySegment() {
          return ms.asSlice(FM$OFFSET$<name>, FM$SIZE$<name>);
        }
        """).replace("<name>", name)
        .indent(2).replace("  \n", "\n"));

    out.write((isStatic ? """

        public static MemorySegment <name>$MemorySegment(
            <receiverAndParams>) {
          return ms.asSlice(
              <offsetArgument>
              FM$ELEMENT_LAYOUT$<name>.byteSize());
        }
        """ : """

        public MemorySegment <name>$MemorySegment(<receiverAndParams>) {
          return ms.asSlice(
              <offsetArgument>
              FM$ELEMENT_LAYOUT$<name>.byteSize());
        }
        """)
        .replace("<name>", name)
        .replace("<receiverAndParams>", receiverAndParams)
        .replace("<offsetArgument>", offsetArgument)
        .indent(2).replace("  \n", "\n"));
  }

  private void writeIndexedAddressHelpers(Writer out, String className,
      IndexedField indexed) throws IOException {
    var name = indexed.name();
    var params = indexed.parameterDeclarations();
    var args = indexed.parameterNames();
    var vhArgs = "ms, " + args;
    var valueName = unusedIndexedParameterName(indexed, "value");

    out.write("""

          public MemorySegment <name>$Address(<params>) {
            return (MemorySegment) FM$VH$<name>.get(<vhArgs>);
          }

          public <class> <name>$Address(
              <params>, MemorySegment <valueName>) {
            FM$VH$<name>.set(<vhArgs>,
                <valueName> == null ? MemorySegment.NULL : <valueName>);
            return this;
          }
        """
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<params>", params)
        .replace("<valueName>", valueName)
        .replace("<vhArgs>", vhArgs));
  }

  private String indexedGetterExpression(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var field = indexed.element();
    var name = indexed.name();
    var vhArgs = segment + ", " + args;
    if (indexed.primitive()) {
      return """
          (<type>) FM$VH$<name>.get(<arguments>)
          """
          .replace("<type>", field.typeName())
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .strip();
    }

    if (field.isMemorySegment()) {
      if (isStatic) {
        throw new IllegalStateException(
            "Record snapshots cannot contain MemorySegment arrays");
      }
      return """
          <name>$Address(<arguments>)
          """
          .replace("<name>", name)
          .replace("<arguments>", args)
          .strip();
    }

    if (indexed.structuredValueElement()) {
      var foreignClass = foreignMemoryClassName(field.typeElement);
      var leaf = indexedElementSegmentCall(
          indexed, segment, args, isStatic);
      var template = field.isRecord() ? """
          <foreignClass>.fromMemorySegment(
                  <leaf>)
          """ : """
          new <foreignClass>(
                  <leaf>)
          """;
      return template
          .replace("<foreignClass>", foreignClass)
          .replace("<leaf>", leaf)
          .strip();
    }

    throw new IllegalStateException(
        "Unsupported indexed getter: " + indexed.name());
  }

  private String indexedSetterBody(IndexedField indexed, String segment,
      String args, boolean isStatic, boolean withAllocator) {
    var field = indexed.element();
    var name = indexed.name();
    var vhArgs = segment + ", " + args;
    var valueName = unusedIndexedParameterName(indexed, "value");
    var allocatorName = unusedIndexedParameterName(indexed, "allocator");
    if (indexed.primitive()) {
      return """
          FM$VH$<name>.set(<arguments>, <value>);
          """
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName)
          .strip();
    }

    if (field.isMemorySegment()) {
      return """
          FM$VH$<name>.set(<arguments>,
                  <value> == null ? MemorySegment.NULL : <value>);
          """
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName)
          .strip();
    }

    var foreignClass = foreignMemoryClassName(field.typeElement);
    if (indexed.addressElement()) {
      var template = field.isRecord() ? """
          FM$VH$<name>.set(<arguments>,
                  <value> == null
                      ? MemorySegment.NULL
                      : <foreignClass>.toMemorySegment(
                          <allocator>, <value>));
          """ : """
          FM$VH$<name>.set(<arguments>,
                  <value> == null
                      ? MemorySegment.NULL
                      : ((<foreignClass>) <value>).ms);
          """;
      return template
          .replace("<name>", name)
          .replace("<arguments>", vhArgs)
          .replace("<value>", valueName)
          .replace("<foreignClass>", foreignClass)
          .replace("<allocator>", allocatorName)
          .strip();
    }

    var leaf = indexedElementSegmentCall(indexed, segment, args, isStatic);
    if (field.isRecord()) {
      return """
          <foreignClass>.toMemorySegment(
                  <value>, <leaf><allocatorArgument>);
          """
          .replace("<foreignClass>", foreignClass)
          .replace("<value>", valueName)
          .replace("<leaf>", leaf)
          .replace("<allocatorArgument>",
              withAllocator ? ", " + allocatorName : "")
          .strip();
    }

    return """
        MemorySegment.copy(
                ((<foreignClass>) <value>).ms, 0L,
                <leaf>, 0L,
                FM$ELEMENT_LAYOUT$<name>.byteSize());
        """
        .replace("<foreignClass>", foreignClass)
        .replace("<value>", valueName)
        .replace("<leaf>", leaf)
        .replace("<name>", name)
        .strip();
  }

  private String indexedElementSegmentCall(IndexedField indexed,
      String segment, String args, boolean isStatic) {
    var arguments = isStatic ? segment + ", " + args : args;
    return """
        <name>$MemorySegment(<arguments>)
        """
        .replace("<name>", indexed.name())
        .replace("<arguments>", arguments)
        .strip();
  }

  private String unusedIndexedParameterName(
      IndexedField indexed, String preferred) {
    var usedNames = indexed.dimensions().stream()
        .map(IndexedField.Dimension::name)
        .collect(java.util.stream.Collectors.toSet());
    var name = preferred;
    while (usedNames.contains(name))
      name += "$";
    return name;
  }

  private String indexedLeafOffset(IndexedField indexed) {
    var paths = new ArrayList<String>();
    paths.add("""
        FM$PE$<name>
        """
        .replace("<name>", indexed.name())
        .strip());
    for (var dimension : indexed.dimensions()) {
      paths.add("""
          MemoryLayout.PathElement.sequenceElement(<index>)
          """
          .replace("<index>", dimension.name())
          .strip());
    }
    return """
        FM$LAYOUT.byteOffset(
            <paths>)
        """
        .replace("<paths>", String.join(",\n    ", paths))
        .strip();
  }

  private void writeIndexedPrimitiveConveniences(Writer out,
      String className, IndexedField indexed, boolean isStatic)
      throws IOException {
    var name = indexed.name();
    var type = indexed.elementTypeName();
    var dimension = indexed.dimensions().getFirst();
    var indexType = dimension.typeName();
    var segmentCall = name + "$MemorySegment("
        + (isStatic ? "ms" : "") + ")";
    var bufferType = primitiveBufferType(type);
    var bufferConversion = switch (type) {
      case "boolean", "byte" -> "";
      default -> ".as" + capitalize(type) + "Buffer()";
    };

    out.write((isStatic ? """

        public static java.nio.<bufferType> <name>$Buffer(MemorySegment ms) {
          return <segmentCall>.asByteBuffer()
              .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
        }

        public static <type>[] <name>$Array(MemorySegment ms) {
          var value = new <type>[(int) FM$DIMENSION$<name>$0];
          for (<indexType> index = 0; index < value.length; index++) {
            value[(int) index] = <name>(ms, index);
          }
          return value;
        }

        public static <type>[] <name>(MemorySegment ms) {
          return <name>$Array(ms);
        }

        public static void <name>(MemorySegment ms, <type>[] value) {
          java.util.Objects.requireNonNull(value, "value");
          if (value.length != FM$DIMENSION$<name>$0) {
            throw new IllegalArgumentException(
                "<name> length must be " + FM$DIMENSION$<name>$0);
          }
          for (<indexType> index = 0; index < value.length; index++) {
            <name>(ms, index, value[(int) index]);
          }
        }
        """ : """

        public java.nio.<bufferType> <name>$Buffer() {
          return <segmentCall>.asByteBuffer()
              .order(java.nio.ByteOrder.nativeOrder())<bufferConversion>;
        }

        public <type>[] <name>$Array() {
          var value = new <type>[(int) FM$DIMENSION$<name>$0];
          for (<indexType> index = 0; index < value.length; index++) {
            value[(int) index] = <name>(index);
          }
          return value;
        }

        public <class> <name>(<type>[] value) {
          java.util.Objects.requireNonNull(value, "value");
          if (value.length != FM$DIMENSION$<name>$0) {
            throw new IllegalArgumentException(
                "<name> length must be " + FM$DIMENSION$<name>$0);
          }
          for (<indexType> index = 0; index < value.length; index++) {
            <name>(index, value[(int) index]);
          }
          return this;
        }
        """)
        .replace("<bufferType>", bufferType)
        .replace("<bufferConversion>", bufferConversion)
        .replace("<segmentCall>", segmentCall)
        .replace("<indexType>", indexType)
        .replace("<class>", className == null ? "" : className)
        .replace("<name>", name)
        .replace("<type>", type)
        .indent(2).replace("  \n", "\n"));
  }

  private void writeIndexedRecordArrayConveniences(Writer out,
      IndexedField indexed, boolean needsAllocator) throws IOException {
    var name = indexed.name();
    var elementType = indexed.elementTypeName();
    var arrayType = indexed.declaredTypeName();
    var allocatorParameter = needsAllocator
        ? "SegmentAllocator allocator, " : "";
    var allocatorArgument = needsAllocator ? "allocator, " : "";

    out.write("""

        public static <arrayType> <name>$Array(MemorySegment ms) {
          var value = new <elementType>[(int) FM$DIMENSION$<name>$0];
          for (long index = 0; index < value.length; index++) {
            value[(int) index] = <name>(ms, index);
          }
          return value;
        }

        public static <arrayType> <name>(MemorySegment ms) {
          return <name>$Array(ms);
        }

        public static void <name>(MemorySegment ms,
            <allocatorParameter><arrayType> value) {
          java.util.Objects.requireNonNull(value, "value");
          if (value.length != FM$DIMENSION$<name>$0) {
            throw new IllegalArgumentException(
                "<name> length must be " + FM$DIMENSION$<name>$0);
          }
          for (long index = 0; index < value.length; index++) {
            <name>(ms, <allocatorArgument>index, value[(int) index]);
          }
        }
      """
      .replace("<allocatorParameter>", allocatorParameter)
      .replace("<allocatorArgument>", allocatorArgument)
      .replace("<arrayType>", arrayType)
      .replace("<elementType>", elementType)
      .replace("<name>", name));
  }

  private String primitiveBufferType(String primitive) {
    return switch (primitive) {
      case "boolean", "byte" -> "ByteBuffer";
      case "char" -> "CharBuffer";
      case "short" -> "ShortBuffer";
      case "int" -> "IntBuffer";
      case "long" -> "LongBuffer";
      case "float" -> "FloatBuffer";
      case "double" -> "DoubleBuffer";
      default -> throw new IllegalArgumentException(
          "Unexpected primitive: " + primitive);
    };
  }

  private boolean indexedElementNeedsAllocator(IndexedField indexed) {
    var element = indexed.element();
    return (indexed.addressElement() && element.isRecord())
        || (indexed.structuredValueElement() && element.isRecord()
            && recordConverterNeedsAllocator(element.typeElement));
  }

  private boolean indexedArrayNeedsAllocator(IndexedField indexed) {
    return indexedElementNeedsAllocator(indexed);
  }

  private void writeUnsupportedMethods(Writer out,
      List<ExecutableElement> unsupportedMethods) throws IOException {
    for (var method : unsupportedMethods) {
      out.write("""

            <signature> {
              throw new RuntimeException("Check compile errors!");
            }
          """
          .replace("<signature>", sourceMethodSignature(method)));
    }
  }

  private void writeThrowingStaticAccessors(Writer out, VariableGenerator field)
      throws IOException {
    String name = field.name();
    String type = field.typeName();

    out.write("""

          public static <type> <name>(MemorySegment ms) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(MemorySegment ms, <type> value) {
            throw new RuntimeException("Check compile errors!");
          }

          public static void <name>(
              MemorySegment ms, SegmentAllocator allocator, <type> value) {
            throw new RuntimeException("Check compile errors!");
          }
        """
        .replace("<name>", name)
        .replace("<type>", type));
  }

  private void writeThrowingFieldAccessors(Writer out, String className,
      VariableGenerator field, boolean writeSetter) throws IOException {
    String name = field.name();
    String type = field.typeName();
    var setter = writeSetter
        ? """

            public <class> <name>(<type> value) {
              throw new RuntimeException("Check compile errors!");
            }
            """
        : "";

    out.write("""

          public <type> <name>() {
            throw new RuntimeException("Check compile errors!");
          }
          <setter>
        """
        .replace("<setter>", setter)
        .replace("<class>", className)
        .replace("<name>", name)
        .replace("<type>", type));
  }

  /// Writes getter and setter accessors for a simple (non-buffer) field in the
  /// accessor style selected by the target.
  private void writeAccessorsSimple(Writer out, AccessorTarget target,
      VariableGenerator field) throws IOException {
    String name = field.name();

    if (!target.isStatic() && field.isPrimitiveAddress()) {
      reportMemoryBackedPrimitiveAddressField(field);
      writeThrowingFieldAccessors(out, target.className(), field, false);
      return;
    }

    if (fieldAccessorsShouldThrow(target, field)) {
      writeThrowingAccessors(out, target, field);
      return;
    }

    if (field.isPrimitiveAddress()) {
      writeGetter(out, target, field, primitiveAddressGetter(field, "ms"));
      writeSetter(out, target, field, true, """
          var address = allocator.allocate(<layout>);
          address.set(<layout>, 0L, value);
          FM$VH$<name>.set(ms, address);"""
          .replace("<layout>", field.valueLayout())
          .replace("<name>", name));
      return;
    }

    var typeElement = field.typeElement;

    if (isNestedValue(field)) {
      String fieldClassName = foreignMemoryClassName(typeElement);

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeGetter(out, target, field, """
            <foreignClassName>.fromMemorySegment(ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>),
                FM$LAYOUT.select(FM$PE$<name>).byteSize()))"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));

        boolean needsAllocator = recordConverterNeedsAllocator(typeElement);
        writeSetter(out, target, field, needsAllocator, """
            var layout = FM$LAYOUT.select(FM$PE$<name>);
            var slice = ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
            <foreignClassName>.toMemorySegment(value, slice<allocator>);"""
            .replace("<allocator>", needsAllocator ? ", allocator" : "")
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      } else {
        writeGetter(out, target, field, """
            new <foreignClassName>(ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>),
                FM$LAYOUT.select(FM$PE$<name>).byteSize()))"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));

        writeSetter(out, target, field, false, """
            var layout = FM$LAYOUT.select(FM$PE$<name>);
            var slice = ms.asSlice(
                FM$LAYOUT.byteOffset(FM$PE$<name>), layout.byteSize());
            MemorySegment.copy(((<foreignClassName>)value).ms, 0,
                slice, 0, layout.byteSize());"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      }
    } else if (isNestedAddress(field)) {
      if (!target.isStatic()
          && typeElement.getKind() == ElementKind.RECORD) {
        reportMemoryBackedRecordAddressField(field);
        writeThrowingFieldAccessors(out, target.className(), field, false);
        return;
      }

      String fieldClassName = foreignMemoryClassName(typeElement);

      writeGetter(out, target, field, nestedAddressGetter(field, "ms"));

      if (typeElement.getKind() == ElementKind.RECORD) {
        writeSetter(out, target, field, true, """
            FM$VH$<name>.set(ms,
                <foreignClassName>.toMemorySegment(allocator, value));"""
            .replace("<foreignClassName>", fieldClassName)
            .replace("<name>", name));
      } else {
        writeSetter(out, target, field, false,
            "FM$VH$<name>.set(ms, ((<fieldClass>) value).ms);"
                .replace("<fieldClass>", fieldClassName)
                .replace("<name>", name));
      }
    } else {
      writeGetter(out, target, field,
          "(<type>) FM$VH$<name>.get(ms)"
              .replace("<type>", field.typeName())
              .replace("<name>", name));

      writeSetter(out, target, field, false,
          "FM$VH$<name>.set(ms, value);".replace("<name>", name));
    }
  }

  private void writeGetter(Writer out, AccessorTarget target,
      VariableGenerator field, String expression) throws IOException {
    out.write("""

          <head> {
            return <expression>;
          }
        """
        .replace("<head>", target.getterHead(field))
        .replace("<expression>", expression.replace("\n", "\n    ")));
  }

  private void writeSetter(Writer out, AccessorTarget target,
      VariableGenerator field, boolean needsAllocator, String body)
      throws IOException {
    var statements = target.isStatic() ? body : body + "\nreturn this;";

    out.write("""

          <head> {
            <statements>
          }
        """
        .replace("<head>", target.setterHead(field, needsAllocator))
        .replace("<statements>", statements.replace("\n", "\n    ")));
  }

  private void writeThrowingAccessors(Writer out, AccessorTarget target,
      VariableGenerator field) throws IOException {
    if (target.isStatic()) {
      writeThrowingStaticAccessors(out, field);
    } else {
      writeThrowingFieldAccessors(out, target.className(), field, true);
    }
  }

  private void validateFields(List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields) {
    for (var field : fields) {
      if (indexedFields.containsKey(field.name())) {
        if (field.isString()) {
          processingEnv.getMessager().printError(
              "String elements are not supported in indexed fields",
              field.element);
        }
        continue;
      }

      if (field.hasSequenceOnUnsupportedType()) {
        processingEnv.getMessager().printError(
            "@Sequence on interface fields belongs on int or long indexed "
                + "accessor parameters",
            field.element);
        continue;
      }

      if (field.isString()) {
        processingEnv.getMessager().printError(
            "String fields are not supported on @Struct or @Union memory types",
            field.element);
        return;
      }
    }
  }

  private boolean fieldAccessorsShouldThrow(
      AccessorTarget target, VariableGenerator field) {
    return field.isString()
        || field.unsupported()
        || (target.isStatic() && field.isArrayOrBuffer());
  }

  private void reportMemoryBackedRecordAddressField(VariableGenerator field) {
    processingEnv.getMessager().printError(
        "@Address record fields are not supported on memory-backed "
            + "@Struct or @Union interfaces; use an interface struct or "
            + "MemorySegment for persistent pointer fields",
        field.element);
  }

  private void reportMemoryBackedPrimitiveAddressField(
      VariableGenerator field) {
    processingEnv.getMessager().printError(
        "@Address primitive fields are not supported on memory-backed "
            + "@Struct or @Union interfaces; use a memory-backed interface "
            + "type or MemorySegment for persistent pointer fields",
        field.element);
  }

  private boolean isNestedValue(VariableGenerator field) {
    return field.isForeignMemory() && field.isValue();
  }

  private boolean isNestedAddress(VariableGenerator field) {
    return field.isForeignMemory() && field.isAddress();
  }

  private boolean isRecordAddress(VariableGenerator variable) {
    return variable.isRecord() && variable.isAddress();
  }

  private boolean needsAllocatorWrite(VariableGenerator variable) {
    return variable.isPrimitiveAddress()
        || isRecordAddress(variable)
        || (variable.isRecord() && variable.isValue()
            && recordConverterNeedsAllocator(variable.typeElement));
  }

  private boolean recordConverterNeedsAllocator(TypeElement type) {
    return recordConverterNeedsAllocator(type, new HashSet<>());
  }

  private boolean recordConverterNeedsAllocator(
      TypeElement type, Set<String> visited) {
    if (type == null || type.getKind() != ElementKind.RECORD)
      return false;

    var name = type.getQualifiedName().toString();
    if (!visited.add(name))
      return false;

    for (var component : type.getRecordComponents()) {
      var componentType = component.asType();
      var valueType = componentType.getKind() == TypeKind.ARRAY
          ? ((ArrayType) componentType).getComponentType()
          : componentType;
      var componentGen = componentType.getKind() == TypeKind.ARRAY
          ? new VariableGenerator(processingEnv,
              component.getSimpleName().toString(), valueType, 1L, component)
          : new VariableGenerator(processingEnv, component);

      if (componentGen.isPrimitiveAddress())
        return true;

      if (isRecordAddress(componentGen))
        return true;

      if (componentGen.isRecord() && componentGen.isValue()
          && recordConverterNeedsAllocator(componentGen.typeElement, visited))
        return true;
    }

    return false;
  }

  private String nestedAddressGetter(
      VariableGenerator field, String segment) {
    return foreignMemoryClassName(field.typeElement)
        + ".reinterpret((MemorySegment) FM$VH$" + field.name()
        + ".get(" + segment + ")" + ")";
  }

  private String primitiveAddressGetter(
      VariableGenerator field, String segment) {
    String layout = field.valueLayout();
    String address = "((MemorySegment) FM$VH$" + field.name()
        + ".get(" + segment + "))";

    return address + ".reinterpret(" + layout + ".byteSize())\n"
        + "    .get(" + layout + ", 0L)";
  }

  private String capitalize(String name) {
    return Character.toTitleCase(name.charAt(0)) + name.substring(1);
  }
}
