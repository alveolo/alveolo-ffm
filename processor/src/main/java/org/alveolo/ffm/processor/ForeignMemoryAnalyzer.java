package org.alveolo.ffm.processor;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;

import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Virtual;

/// Analyzes the fields and conversion requirements of a generated foreign
/// memory type.
final class ForeignMemoryAnalyzer {
  record Fields(
      List<VariableGenerator> fields,
      Map<String, IndexedField> indexedFields,
      List<ExecutableElement> unsupportedMethods
  ) {}

  private final ProcessingEnvironment processingEnv;
  private final Messager messager;

  ForeignMemoryAnalyzer(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    messager = processingEnv.getMessager();
  }

  /// Infers struct fields from interface accessor methods or record components.
  Fields inferFields(TypeElement type, boolean excludeObjectMethods) {
    if (type.getKind() == ElementKind.RECORD)
      return inferRecordFields(type);

    // Group methods by name.
    Map<String, List<ExecutableElement>> methodsByName = new LinkedHashMap<>();
    for (var enclosed : type.getEnclosedElements()) {
      if (enclosed instanceof ExecutableElement method
          && enclosed.getModifiers().contains(ABSTRACT)
          && !enclosed.getModifiers().contains(STATIC)
          && !enclosed.getModifiers().contains(DEFAULT)) {
        if (excludeObjectMethods && isObjectMethod(method)) {
          continue;
        }
        var name = method.getSimpleName().toString();
        methodsByName.computeIfAbsent(name, _ -> new ArrayList<>()).add(method);
      }
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
            "Array-returning interface fields are not supported; declare an "
                + "indexed element accessor whose int or long parameters "
                + "carry @Sequence",
            accessor);
        for (var method : methods) {
          addUnsupportedMethod(unsupportedMethods, method);
        }
        continue;
      }

      var accessorGenerator = new VariableGenerator(processingEnv, fieldName,
          fieldType, TypeGenerator.sequence(fieldType, accessor), accessor);
      var bufferField = accessorGenerator.isNioBuffer();
      if (bufferField) {
        messager.printError(
            "NIO Buffer types are not supported as @Struct or @Union fields; "
                + "declare an indexed element accessor, for example 'int "
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
        fields.add(new VariableGenerator(processingEnv, component));
        continue;
      }

      var indexed = indexedRecordField(component);
      if (indexed == null) {
        // Keep a generated, throwing converter after reporting the focused
        // diagnostic. This avoids secondary "generated class not found"
        // errors in clients that compile alongside the invalid declaration.
        fields.add(new VariableGenerator(processingEnv, component));
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

      if (!TypeGenerator.hasSequence(parameter.asType(), parameter)) {
        messager.printError(
            "Each indexed field parameter must carry @Sequence", parameter);
        valid = false;
        continue;
      }

      var size = TypeGenerator.sequence(parameter.asType(), parameter);
      if (size <= 0) {
        messager.printError(
            "Indexed field @Sequence value must be positive", parameter);
        valid = false;
      }
      if (kind == TypeKind.INT && size > Integer.MAX_VALUE) {
        messager.printError(
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
      messager.printError(
          "Place @Sequence on each indexed field parameter, not on the "
              + "accessor return",
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
          "Record array field @Sequence value cannot exceed "
              + "Integer.MAX_VALUE",
          component);
      valid = false;
    }

    var element = new VariableGenerator(processingEnv,
        component.getSimpleName().toString(), componentType, 1L, component);
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
    if (element.isPrimitiveAddress()) {
      messager.printError(
          "@Address primitive array elements are not supported",
          element.element);
      return false;
    }

    if (element.isPrimitive()) return true;

    if (recordSnapshot) {
      if (!element.isForeignMemory()
          || !element.isRecord()
          || !element.isValue()) {
        messager.printError(
            "Record arrays support primitives and value-style @Struct "
                + "record elements only",
            element.element);
        return false;
      }
      return true;
    }

    if (element.isMemorySegment()) return true;
    if (element.isForeignMemory()
        && (element.isValue() || element.isAddress()))
      return true;

    messager.printError(
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

  /// Buffer snapshots are intentionally not part of the record model. Array
  /// snapshots are validated while inferring their element and dimensions.
  void validateRecordComponents(TypeElement type) {
    if (type.getKind() != ElementKind.RECORD) return;

    for (var component : type.getRecordComponents()) {
      var componentType = component.asType();
      if (componentType.getKind() == TypeKind.ARRAY) {
        continue;
      }
      if (new TypeGenerator(processingEnv, componentType).isNioBuffer()) {
        messager.printError(
            "NIO Buffer types are not supported as record components; "
                + "use a one-dimensional array component annotated "
                + "@Sequence",
            component);
      }
    }
  }

  void validateFields(Fields fields) {
    for (var field : fields.fields()) {
      if (fields.indexedFields().containsKey(field.name())) {
        if (field.isString()) {
          messager.printError(
              "String elements are not supported in indexed fields",
              field.element);
        }
        continue;
      }

      if (field.hasSequenceOnUnsupportedType()) {
        messager.printError(
            "@Sequence on interface fields belongs on int or long indexed "
                + "accessor parameters",
            field.element);
        continue;
      }

      if (field.isString()) {
        messager.printError(
            "String fields are not supported on @Struct or @Union memory "
                + "types",
            field.element);
        return;
      }
    }
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
      var componentGenerator = componentType.getKind() == TypeKind.ARRAY
          ? new VariableGenerator(processingEnv,
              component.getSimpleName().toString(), valueType, 1L, component)
          : new VariableGenerator(processingEnv, component);

      if (componentGenerator.isPrimitiveAddress())
        return true;

      if (isRecordAddress(componentGenerator))
        return true;

      if (componentGenerator.isRecord() && componentGenerator.isValue()
          && recordConverterNeedsAllocator(
              componentGenerator.typeElement, visited))
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

  boolean indexedArrayNeedsAllocator(IndexedField indexed) {
    return indexedElementNeedsAllocator(indexed);
  }

  private boolean isRecordAddress(VariableGenerator variable) {
    return variable.isRecord() && variable.isAddress();
  }
}
