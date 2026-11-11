package org.alveolo.ffm.processor;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.alveolo.ffm.Flexible;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

/// Analyzes the fields and conversion requirements of a generated foreign
/// memory type.
final class ForeignMemoryAnalyzer {
  record Fields(
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields,
      List<ExecutableElement> unsupportedMethods
  ) {}

  record ValidatedFields(Fields fields, FlexibleArrayGenerator flexible) {}

  private final ProcessingEnvironment processingEnv;
  private final GeneratedTypeRegistry generatedTypes;
  private final Messager messager;
  private final Map<TypeElement, Boolean> zeroSizedLayouts = new HashMap<>();

  ForeignMemoryAnalyzer(ProcessingEnvironment processingEnv,
      GeneratedTypeRegistry generatedTypes) {
    this.processingEnv = processingEnv;
    this.generatedTypes = generatedTypes;
    messager = processingEnv.getMessager();
  }

  /// Infers struct fields from interface accessor methods or record components.
  Fields inferFields(TypeElement type) {
    if (type.getKind() == ElementKind.RECORD)
      return inferRecordFields(type);

    return inferFields(declaredFieldMethods(type));
  }

  private static List<ExecutableElement> declaredFieldMethods(TypeElement type) {
    return type.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(method -> method.getModifiers().contains(ABSTRACT))
        .filter(method -> !method.getModifiers().contains(STATIC))
        .filter(method -> !method.getModifiers().contains(DEFAULT))
        .toList();
  }

  boolean hasZeroSizedLayout(TypeElement type) {
    var struct = type.getAnnotation(Struct.class);
    if (struct == null && type.getAnnotation(Union.class) == null) return false;

    // Leave recursive by-value layouts to LayoutCycleValidator.
    var known = zeroSizedLayouts.putIfAbsent(type, false);
    if (known != null) return known;

    boolean zeroSized;
    if (type.getKind() == ElementKind.RECORD) {
      zeroSized = type.getRecordComponents().stream()
          .allMatch(component -> zeroSizedField(component, component.asType()));
    } else {
      Stream<ExecutableElement> fields;
      if (struct == null) {
        fields = declaredFieldMethods(type).stream();
      } else {
        var model = generatedTypes.struct(type).resolve();
        if (!model.valid() || model.vtable() || model.flexible() != null) return false;
        fields = Stream.concat(model.baseFieldMethods().stream(), model.fieldMethods().stream());
      }
      zeroSized = fields.allMatch(
          method -> zeroSizedField(method, method.getReturnType()));
    }
    zeroSizedLayouts.put(type, zeroSized);
    return zeroSized;
  }

  private boolean zeroSizedField(Element member, TypeMirror type) {
    if (type instanceof ArrayType array) type = array.getComponentType();
    return type instanceof DeclaredType
        && isZeroSizedValue(
            new TypeGenerator(processingEnv, generatedTypes, type, member));
  }

  boolean isZeroSizedValue(TypeGenerator value) {
    if (!value.foreignMemory || !value.isValue()) return false;
    var specification = value.layoutSpecification();
    return specification != null && hasZeroSizedLayout(specification);
  }

  String zeroSizedValueError(TypeGenerator value) {
    if (isZeroSizedValue(value))
      return "Zero-sized native types can only be used by address";

    if (value.isValueStructRecordArray()) {
      var element = value.arrayComponentGenerator();
      if (isZeroSizedValue(element))
        return "Native array elements must have nonzero size: " + element.typeName();
    }
    return null;
  }

  Fields inferFields(List<ExecutableElement> declarations) {
    // Group methods by name.
    Map<String, List<ExecutableElement>> methodsByName = new LinkedHashMap<>();
    for (var method : declarations) {
      var name = method.getSimpleName().toString();
      methodsByName.computeIfAbsent(name, _ -> new ArrayList<>()).add(method);
    }

    var fields = new ArrayList<VariableGenerator>();
    var indexedFields = new LinkedHashMap<String, IndexedField>();
    var unsupportedMethods = new ArrayList<ExecutableElement>();
    for (var entry : methodsByName.entrySet()) {
      var fieldName = entry.getKey();
      var methods = entry.getValue();

      ExecutableElement accessor = null;
      var indexedCandidates = new ArrayList<ExecutableElement>();
      var fieldMethods = new ArrayList<ExecutableElement>();

      for (var method : methods) {
        var parameters = method.getParameters();
        if (parameters.isEmpty()
            && method.getReturnType().getKind() != TypeKind.VOID) {
          accessor = method;
        } else if (!parameters.isEmpty()
            && method.getReturnType().getKind() != TypeKind.VOID) {
          indexedCandidates.add(method);
        } else {
          fieldMethods.add(method);
        }
      }

      if (accessor == null && indexedCandidates.size() == 1
          && fieldMethods.isEmpty()) {
        var indexedMethod = indexedCandidates.getFirst();
        var indexed = indexedInterfaceField(indexedMethod);
        if (indexed != null) {
          fields.add(indexed.element());
          indexedFields.put(fieldName, indexed);
        } else {
          addUnsupportedMethod(unsupportedMethods, indexedMethod);
        }
        continue;
      }

      if (accessor == null) {
        if (indexedCandidates.size() > 1) {
          for (var candidate : indexedCandidates) {
            messager.printError(
                "Field '" + fieldName
                    + "' has multiple indexed accessor declarations",
                candidate);
          }
        } else {
          messager.printError(
              "Field '" + fieldName + "' has no accessor",
              methods.getFirst());
        }
        for (var method : methods) {
          addUnsupportedMethod(unsupportedMethods, method);
        }
        continue;
      }

      // Determine type from accessor return.
      var fieldType = accessor.getReturnType();
      if (fieldType.getKind() == TypeKind.ARRAY) {
        messager.printError(
            "Array-returning interface fields are not supported; declare an"
                + " indexed element accessor whose int or long parameters"
                + " carry @Sequence",
            accessor);
        for (var method : methods) {
          addUnsupportedMethod(unsupportedMethods, method);
        }
        continue;
      }

      var accessorGenerator = new VariableGenerator(
          processingEnv, generatedTypes, fieldName, fieldType,
          TypeGenerator.sequence(fieldType, accessor), accessor);
      var bufferField = accessorGenerator.isNioBuffer();
      if (bufferField) {
        messager.printError(
            "NIO Buffer types are not supported as @Struct or @Union fields;"
                + " declare an indexed element accessor, for example 'int "
                + fieldName + "(@Sequence(N) long index)'",
            accessor);
      }

      fieldMethods.addAll(indexedCandidates);
      for (var method : fieldMethods) {
        if (!bufferField) {
          messager.printError(
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

    return new Fields(fields, indexedFields, unsupportedMethods);
  }

  private Fields inferRecordFields(TypeElement record) {
    var fields = new ArrayList<VariableGenerator>();
    var indexedFields = new LinkedHashMap<String, IndexedField>();

    for (var component : record.getRecordComponents()) {
      if (component.asType().getKind() != TypeKind.ARRAY) {
        fields.add(new VariableGenerator(
            processingEnv, generatedTypes, component));
        continue;
      }

      var indexed = indexedRecordField(component);
      if (indexed == null) {
        // Keep a generated, throwing converter after reporting the focused
        // diagnostic. This avoids secondary "generated class not found"
        // errors in clients that compile alongside the invalid declaration.
        fields.add(new VariableGenerator(
            processingEnv, generatedTypes, component));
        continue;
      }

      fields.add(indexed.element());
      indexedFields.put(indexed.element().name(), indexed);
    }

    return new Fields(fields, indexedFields, List.of());
  }

  private IndexedField indexedInterfaceField(ExecutableElement accessor) {
    var valid = true;
    var dimensions = new ArrayList<IndexedField.Dimension>();
    for (var parameter : accessor.getParameters()) {
      var kind = parameter.asType().getKind();
      if (kind != TypeKind.INT && kind != TypeKind.LONG) {
        messager.printError(
            "Indexed field parameters must be int or long", parameter);
        valid = false;
      }

      var flexible = parameter.getAnnotation(Flexible.class) != null;
      if (flexible) {
        if (!parameter.equals(accessor.getParameters().getFirst())) {
          messager.printError("@Flexible must be the outermost dimension", parameter);
          valid = false;
        }
        if (TypeGenerator.hasSequence(parameter.asType(), parameter)) {
          messager.printError(
              "@Flexible cannot share an index with @Sequence", parameter);
          valid = false;
        }
      } else if (!TypeGenerator.hasSequence(parameter.asType(), parameter)) {
        messager.printError(
            "Each indexed field parameter must carry @Sequence", parameter);
        valid = false;
        continue;
      }

      var size = flexible ? 0L : TypeGenerator.sequence(parameter.asType(), parameter);
      if (!flexible && size <= 0) {
        messager.printError(
            "Indexed field @Sequence value must be positive", parameter);
        valid = false;
      }
      if (kind == TypeKind.INT && size > Integer.MAX_VALUE) {
        messager.printError(
            "An int indexed field parameter cannot address a dimension"
                + " larger than Integer.MAX_VALUE",
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
        processingEnv, generatedTypes, name, type, 1L, accessor);

    if (TypeGenerator.hasSequence(type, accessor)) {
      messager.printError(
          "Place @Sequence on each indexed field parameter, not on the"
              + " accessor return",
          accessor);
      valid = false;
    }

    if (type.getKind() == TypeKind.ARRAY || element.isNioBuffer()) {
      messager.printError(
          "Indexed fields must return one element, not an array or Buffer",
          accessor);
      valid = false;
    }

    if (!validIndexedElement(element, false)) {
      valid = false;
    }
    if (dimensions.size() == 1 && element.isPrimitive()
        && dimensions.getFirst().size() > Integer.MAX_VALUE) {
      messager.printError(
          "One-dimensional primitive indexed fields cannot exceed"
              + " Integer.MAX_VALUE because Java array and Buffer views use"
              + " int sizes",
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
      messager.printError(
          "Multidimensional Java array record fields are not supported",
          component);
      valid = false;
    }

    if (!TypeGenerator.hasSequence(component.asType(), component)) {
      messager.printError(
          "Record array fields must carry one positive @Sequence",
          component);
      valid = false;
    }

    var size = TypeGenerator.sequence(component.asType(), component);
    if (size <= 0) {
      messager.printError(
          "Record array field @Sequence value must be positive", component);
      valid = false;
    }
    if (size > Integer.MAX_VALUE) {
      messager.printError(
          "Record array field @Sequence value cannot exceed Integer.MAX_VALUE",
          component);
      valid = false;
    }

    var element = new VariableGenerator(
        processingEnv, generatedTypes, component.getSimpleName().toString(),
        componentType, 1L, component);
    if (!validIndexedElement(element, true)) {
      valid = false;
    }
    if (!valid) return null;

    var dimension = new IndexedField.Dimension("index", "long", size);

    return new IndexedField(element, component.asType(),
        List.of(dimension), component, true);
  }

  private boolean validIndexedElement(
      VariableGenerator element, boolean recordSnapshot) {
    var flexibleError = element.flexibleUseError(true);
    if (flexibleError != null) {
      messager.printError(flexibleError, element.element);
      return false;
    }

    if (isZeroSizedValue(element)) {
      messager.printError(
          "Native array elements must have nonzero size: " + element.typeName(),
          element.element);
      return false;
    }

    if (element.hasCanonicalScalar()) {
      messager.printError(
          "Canonical C scalar array and indexed-field elements are not yet "
              + "supported",
          element.element);
      return false;
    }

    if (element.isPrimitiveAddress()) {
      messager.printError(
          "@Address primitive array elements are not supported",
          element.element);
      return false;
    }

    if (element.isPrimitive()) return true;

    if (recordSnapshot) {
      if (element.foreignMemory && element.isRecord() && element.isValue())
        return true;

      messager.printError(
          "Record arrays support primitives and"
              + " value-style @Struct record elements only",
          element.element);
      return false;
    }

    if (element.isMemorySegment())
      return true;

    if (element.foreignMemory) {
      if (element.isValue() || element.isAddress())
        return true;
    }

    messager.printError(
        "Indexed fields support primitives, MemorySegment,"
            + " and @Struct or @Union elements",
        element.element);

    return false;
  }

  private void addUnsupportedMethod(
      List<ExecutableElement> unsupportedMethods, ExecutableElement method) {
    if (!unsupportedMethods.contains(method)) {
      unsupportedMethods.add(method);
    }
  }

  /// Records are snapshots, never containers for memory-backed wrappers.
  /// Enforce this at the declaration, regardless of pass mode or call usage:
  /// otherwise a converted record can retain a view into a closed call arena.
  /// Array snapshots are validated while inferring their element and dimensions.
  void validateRecordComponents(TypeElement type) {
    if (type.getKind() != ElementKind.RECORD) return;

    for (var component : type.getRecordComponents()) {
      var componentType = component.asType();
      if (componentType.getKind() == TypeKind.ARRAY) {
        continue;
      }
      var generator = new TypeGenerator(processingEnv, generatedTypes,
          componentType, component);
      if (generator.foreignMemory && !generator.isRecord()) {
        messager.printError(
            "Record structs cannot contain memory-backed types;"
                + " use a record struct component or declare the containing"
                + " struct as an interface",
            component);
      } else if (generator.isNioBuffer()) {
        messager.printError(
            "NIO Buffer types are not supported as record components;"
                + " use a one-dimensional array component annotated @Sequence",
            component);
      }
    }
  }

  ValidatedFields validateFields(TypeElement type, Fields fields,
      StructInterfaceModel.Result model) {
    var validated = validateFlexibleFields(fields, model);
    fields = validated.fields();
    for (var field : fields.fields()) {
      var flexibleError = field.flexibleUseError(true);
      if (flexibleError != null) {
        messager.printError(flexibleError, field.element);
        continue;
      }
      if (isZeroSizedValue(field)) {
        messager.printError(
            "Zero-sized native types can only be used by address", field.element);
        continue;
      }
      field.bindingAnnotations.validate(processingEnv, type, field.element);
      BindingAnnotations.validateNested(processingEnv, field.typeMirror, type, field.element);
      if (field.releaseMethod() != null) {
        messager.printError("@Release is only supported on String or @Address record return types, not fields",
            field.element);
      }
      var canonicalError = field.canonicalScalarError();
      if (canonicalError != null) {
        messager.printError(canonicalError, field.element);
        continue;
      }

      if (field.unsupported()) {
        messager.printError(
            "Type is not supported: " + field.typeName(), field.element);
        continue;
      }

      var indexed = fields.indexedFields().get(field.name());
      if (indexed != null) {
        if (field.isString()) {
          messager.printError(
              "String elements are not supported in indexed fields",
              field.element);
        }
        continue;
      }

      if (field.hasSequenceOnUnsupportedType()) {
        messager.printError(
            "@Sequence on interface fields belongs on int or long indexed"
                + " accessor parameters",
            field.element);
        continue;
      }

      if (field.isString()) {
        messager.printError(
            "String fields are not supported on @Struct or @Union memory types",
            field.element);
        continue;
      }

      if (type.getKind() == ElementKind.INTERFACE) {
        if (field.isPrimitiveAddress()) {
          messager.printError(
              "@Address primitive fields are not supported on memory-backed "
                  + "@Struct or @Union interfaces; use a memory-backed interface "
                  + "type or MemorySegment for persistent pointer fields",
              field.element);
        } else if (field.foreignMemory && isRecordAddress(field)) {
          messager.printError(
              "@Address record fields are not supported on memory-backed "
                  + "@Struct or @Union interfaces; use an interface struct or "
                  + "MemorySegment for persistent pointer fields",
              field.element);
        }
      }
    }
    return validated;
  }

  private ValidatedFields validateFlexibleFields(Fields fields,
      StructInterfaceModel.Result model) {
    FlexibleArrayGenerator flexible = null;
    var candidates = fields.indexedFields().values().stream()
        .filter(IndexedField::flexible)
        .toList();
    for (var array : candidates) {
      var resolved = flexibleArray(array, fields, model);
      if (resolved == null) {
        fields = withoutIndexedField(fields, array);
      } else {
        flexible = resolved;
      }
    }
    return new ValidatedFields(fields, flexible);
  }

  private FlexibleArrayGenerator flexibleArray(IndexedField array, Fields fields,
      StructInterfaceModel.Result model) {
    String error = null;
    if (model == null) {
      error = "@Flexible is only allowed on the last field of a @Struct interface";
    } else if (model.baseStruct() != null) {
      error = "Physical @Struct inheritance requires fixed-size structs";
    } else if (array.element() != fields.fields().getLast()) {
      error = "@Flexible is only allowed on the last field of a @Struct interface";
    } else if (!model.vtable() && fields.fields().size() == 1) {
      error = "A flexible member requires a preceding header field";
    }
    if (error != null) {
      messager.printError(error, array.declaration());
      return null;
    }

    var declaration = (ExecutableElement) array.declaration();
    var annotation = declaration.getParameters().getFirst()
        .getAnnotation(Flexible.class);
    var countName = annotation.value();
    if (countName.isEmpty()) return new FlexibleArrayGenerator(array, null);
    var count = fields.fields().stream().filter(field -> field.name().equals(countName))
        .findFirst().orElse(null);
    if (count != null && !fields.indexedFields().containsKey(countName)
        && !count.isPrimitiveAddress()) {
      switch (count.typeMirror.getKind()) {
        case BYTE, SHORT, CHAR, INT, LONG -> { return new FlexibleArrayGenerator(array, count); }
      }
    }
    messager.printError("@Flexible count '" + countName
        + "' must name an integral scalar header field", array.declaration());
    return null;
  }

  private Fields withoutIndexedField(Fields fields, IndexedField indexed) {
    var remaining = fields.fields().stream()
        .filter(field -> field != indexed.element())
        .toList();
    var indexedFields = new LinkedHashMap<>(fields.indexedFields());
    indexedFields.remove(indexed.element().name());
    var unsupported = new ArrayList<>(fields.unsupportedMethods());
    addUnsupportedMethod(unsupported, (ExecutableElement) indexed.declaration());
    return new Fields(remaining, indexedFields, unsupported);
  }

  boolean needsAllocatorWrite(VariableGenerator variable) {
    return variable.isPrimitiveAddress()
        || isRecordAddress(variable)
        || (variable.isRecord() && variable.isValue()
            && recordConverterNeedsAllocator(variable.typeElement));
  }

  boolean recordConverterNeedsAllocator(TypeElement type) {
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
          ? new VariableGenerator(processingEnv, generatedTypes,
              component.getSimpleName().toString(), valueType, 1L, component)
          : new VariableGenerator(processingEnv, generatedTypes, component);

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

  boolean indexedElementNeedsAllocator(IndexedField indexed) {
    var element = indexed.element();
    return (indexed.addressElement() && element.isRecord())
        || (indexed.structuredValueElement() && element.isRecord()
            && recordConverterNeedsAllocator(element.typeElement));
  }

  private boolean isRecordAddress(VariableGenerator variable) {
    return variable.isRecord() && variable.isAddress();
  }
}
