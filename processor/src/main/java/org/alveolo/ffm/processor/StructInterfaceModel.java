package org.alveolo.ffm.processor;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

import org.alveolo.ffm.Fields;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Virtual;

/// Resolves immutable field, virtual, and symbol mappings across one struct
/// interface hierarchy.
final class StructInterfaceModel {
  record Result(
      TypeElement baseStruct,
      List<ExecutableElement> fieldMethods,
      List<ExecutableElement> baseFieldMethods,
      ObjectMethodsGenerator.Methods objectMethods,
      boolean vtable,
      boolean valid
  ) {}

  private enum Kind { FIELD, VIRTUAL, SYMBOL }

  private static final class Family {
    final List<ExecutableElement> declarations = new ArrayList<>();
    Mapping mapping;
    boolean invalid;
  }

  private record Mapping(Kind kind, ExecutableElement method) {}

  private record FieldGroup(
      String name, List<Family> families,
      List<ExecutableElement> methods, TypeElement owner) {}

  private final ProcessingEnvironment processingEnv;
  private final List<Family> families = new ArrayList<>();
  private final List<FieldGroup> fieldGroups = new ArrayList<>();
  private final LinkedHashMap<String, FieldGroup> fieldsByName =
      new LinkedHashMap<>();
  private final List<Mapping> objectMappings = new ArrayList<>();
  private final Set<String> visitedTypes = new HashSet<>();
  private boolean valid = true;

  StructInterfaceModel(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  Result analyze(TypeElement target) {
    var baseStruct = baseStruct(target);
    visit(target);
    validateMappedOverrides();
    validateMappedMethods(target);
    validateVirtualSlots();

    var baseFamilies = baseStruct == null
        ? Set.<Family>of() : memberFamilies(baseStruct);
    var fieldMethods = new ArrayList<ExecutableElement>();
    var baseFieldMethods = new ArrayList<ExecutableElement>();
    for (var field : fieldGroups) {
      var inherited = field.families().stream()
          .allMatch(baseFamilies::contains);
      var partlyInherited = field.families().stream()
          .anyMatch(baseFamilies::contains);
      if (partlyInherited && !inherited) {
        error("Field '" + field.name()
            + "' mixes inherited @Struct and local declarations",
            field.owner());
        continue;
      }
      if (inherited) {
        baseFieldMethods.addAll(field.methods());
      } else {
        fieldMethods.addAll(field.methods());
      }
    }

    var methods = new ArrayList<ExecutableElement>();
    var virtualMethods = new ArrayList<ExecutableElement>();
    var symbolMethods = new ArrayList<ExecutableElement>();
    for (var mapping : objectMappings) {
      var family = family(mapping.method());
      if (baseFamilies.contains(family)) continue;

      methods.add(mapping.method());
      if (mapping.kind() == Kind.VIRTUAL)
        virtualMethods.add(mapping.method());
      if (mapping.kind() == Kind.SYMBOL)
        symbolMethods.add(mapping.method());
    }

    var struct = target.getAnnotation(Struct.class);
    var vtable = struct.vtable();
    if (baseStruct != null) {
      var baseVtable = baseStruct.getAnnotation(Struct.class).vtable();
      if (vtable && !baseVtable) {
        error("A derived @Struct cannot add a vtable to its inherited "
            + "layout", target);
      }
      vtable = baseVtable;
    }

    if (!vtable) {
      for (var mapping : objectMappings) {
        if (mapping.kind() == Kind.VIRTUAL) {
          error("@Virtual is only allowed on @Struct(vtable = true) methods",
              mapping.method());
        }
      }
    }

    return new Result(baseStruct, List.copyOf(fieldMethods),
        List.copyOf(baseFieldMethods),
        new ObjectMethodsGenerator.Methods(
            methods, virtualMethods, symbolMethods),
        vtable, valid);
  }

  private void visit(TypeElement type) {
    var name = type.getQualifiedName().toString();
    if (!visitedTypes.add(name)) return;

    var types = processingEnv.getTypeUtils();
    for (var supertype : type.getInterfaces()) {
      if (types.asElement(supertype) instanceof TypeElement parent)
        visit(parent);
    }

    var directMethods = abstractMethods(type.getEnclosedElements());
    for (var method : directMethods) {
      var family = family(method);
      addDeclaration(family, method);
      mapObjectMethod(family, method);
    }

    var fields = type.getAnnotation(Fields.class);
    if (fields != null && type.getAnnotation(Struct.class) == null)
      mapFields(type, fields);

    if (type.getAnnotation(Struct.class) != null)
      mapDirectStructFields(type, directMethods);
  }

  private void mapObjectMethod(Family family, ExecutableElement method) {
    var virtual = method.getAnnotation(Virtual.class);
    var symbol = method.getAnnotation(Symbol.class);
    if (virtual == null && symbol == null) return;

    if (virtual != null && symbol != null) {
      error("@Virtual and @Symbol cannot be used on the same method", method);
      family.invalid = true;
      return;
    }

    var kind = virtual != null ? Kind.VIRTUAL : Kind.SYMBOL;
    if (!introduce(family, kind, method)) return;
    objectMappings.add(family.mapping);
  }

  private void mapFields(TypeElement owner, Fields annotation) {
    var seen = new HashSet<String>();
    for (var name : annotation.value()) {
      if (!seen.add(name)) {
        error("Duplicate @Fields name: " + name, owner);
        continue;
      }

      var methods = abstractMethods(
          processingEnv.getElementUtils().getAllMembers(owner)).stream()
          .filter(method -> method.getSimpleName().contentEquals(name))
          .toList();
      var mappedFamilies = methods.stream()
          .map(this::family)
          .distinct()
          .toList();
      if (mappedFamilies.isEmpty()) {
        error("@Fields method not found: " + name, owner);
        continue;
      }

      if (fieldsByName.containsKey(name)) {
        error("Field mapping is already defined: " + name, owner);
        continue;
      }
      if (mappedFamilies.stream().anyMatch(family -> family.mapping != null)) {
        error("Method mapping is already defined: " + name, owner);
        continue;
      }

      var field = new FieldGroup(
          name, mappedFamilies, methods, owner);
      fieldsByName.put(name, field);
      fieldGroups.add(field);
      for (var index = 0; index < mappedFamilies.size(); index++) {
        var family = mappedFamilies.get(index);
        var method = methods.stream()
            .filter(candidate -> family(candidate) == family)
            .findFirst().orElseThrow();
        introduce(family, Kind.FIELD, method);
      }
    }
  }

  private void mapDirectStructFields(
      TypeElement owner, List<ExecutableElement> methods) {
    var byName = new LinkedHashMap<String, List<ExecutableElement>>();
    for (var method : methods) {
      var family = family(method);
      if (family.mapping != null || family.invalid) continue;

      byName.computeIfAbsent(method.getSimpleName().toString(),
          _ -> new ArrayList<>()).add(method);
    }

    for (var entry : byName.entrySet()) {
      var name = entry.getKey();
      if (fieldsByName.containsKey(name)) {
        error("Field mapping is already defined: " + name, owner);
        continue;
      }

      var mappedFamilies = entry.getValue().stream()
          .map(this::family)
          .distinct()
          .toList();
      var field = new FieldGroup(
          name, mappedFamilies, List.copyOf(entry.getValue()), owner);
      fieldsByName.put(name, field);
      fieldGroups.add(field);
      for (var method : entry.getValue())
        introduce(family(method), Kind.FIELD, method);
    }
  }

  private boolean introduce(Family family, Kind kind,
      ExecutableElement method) {
    if (family.mapping != null) {
      error("Method mapping is already defined by "
          + mappingName(family.mapping.kind()), method);
      return false;
    }

    family.mapping = new Mapping(kind, method);
    return true;
  }

  private void validateMappedMethods(TypeElement target) {
    for (var method : abstractMethods(
        processingEnv.getElementUtils().getAllMembers(target))) {
      var family = family(method);
      if (family.mapping == null && !family.invalid) {
        error("Inherited abstract method must be mapped by @Fields or "
            + "overridden directly on @Struct", method);
      }
    }
  }

  private void validateMappedOverrides() {
    var types = processingEnv.getTypeUtils();
    for (var family : families) {
      if (family.mapping == null) continue;

      var mapped = family.mapping.method();
      for (var declaration : family.declarations) {
        if (!declaration.equals(mapped)
            && !types.isSameType(declaration.getReturnType(),
                mapped.getReturnType())) {
          error("A mapped method override must preserve its return type",
              declaration);
        }
      }
    }
  }

  private void validateVirtualSlots() {
    var slots = new LinkedHashMap<Integer, ExecutableElement>();
    for (var mapping : objectMappings) {
      if (mapping.kind() != Kind.VIRTUAL) continue;

      var method = mapping.method();
      var slot = method.getAnnotation(Virtual.class).value();
      if (slot < 0) {
        error("@Virtual value must be non-negative", method);
        continue;
      }

      var previous = slots.putIfAbsent(slot, method);
      if (previous != null) {
        error("Duplicate @Virtual slot: " + slot, method);
        error("Duplicate @Virtual slot: " + slot, previous);
      }
    }
  }

  private TypeElement baseStruct(TypeElement target) {
    var candidates = new ArrayList<TypeElement>();
    collectStructs(target, target, new HashSet<>(), candidates);
    var types = processingEnv.getTypeUtils();
    var closest = candidates.stream()
        .filter(candidate -> candidates.stream().noneMatch(other ->
            candidate != other
                && types.isSubtype(other.asType(), candidate.asType())))
        .toList();

    if (closest.size() > 1) {
      error("A @Struct may inherit from only one @Struct base", target);
      return null;
    }
    return closest.isEmpty() ? null : closest.getFirst();
  }

  private void collectStructs(TypeElement target, TypeElement type,
      Set<String> visited, List<TypeElement> result) {
    var name = type.getQualifiedName().toString();
    if (!visited.add(name)) return;

    if (type != target && type.getAnnotation(Struct.class) != null)
      result.add(type);

    var types = processingEnv.getTypeUtils();
    for (var supertype : type.getInterfaces()) {
      if (!(types.asElement(supertype) instanceof TypeElement parent))
        continue;
      collectStructs(target, parent, visited, result);
    }
  }

  private Set<Family> memberFamilies(TypeElement type) {
    var result = new LinkedHashSet<Family>();
    for (var method : abstractMethods(
        processingEnv.getElementUtils().getAllMembers(type)))
      result.add(family(method));
    return result;
  }

  private Family family(ExecutableElement method) {
    for (var family : families) {
      if (family.declarations.stream()
          .anyMatch(declaration -> sameFamily(method, declaration))) {
        addDeclaration(family, method);
        return family;
      }
    }

    var family = new Family();
    family.declarations.add(method);
    families.add(family);
    return family;
  }

  private boolean sameFamily(
      ExecutableElement left, ExecutableElement right) {
    if (!left.getSimpleName().contentEquals(right.getSimpleName()))
      return false;

    var types = processingEnv.getTypeUtils();
    var leftType = (ExecutableType) left.asType();
    var rightType = (ExecutableType) right.asType();
    return types.isSubsignature(leftType, rightType)
        || types.isSubsignature(rightType, leftType);
  }

  private void addDeclaration(Family family, ExecutableElement method) {
    if (!family.declarations.contains(method))
      family.declarations.add(method);
  }

  private static List<ExecutableElement> abstractMethods(
      List<? extends javax.lang.model.element.Element> elements) {
    return elements.stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(method -> method.getModifiers().contains(ABSTRACT))
        .filter(method -> !method.getModifiers().contains(STATIC))
        .filter(method -> !method.getModifiers().contains(DEFAULT))
        .toList();
  }

  private static String mappingName(Kind kind) {
    return switch (kind) {
      case FIELD -> "@Fields or @Struct";
      case VIRTUAL -> "@Virtual";
      case SYMBOL -> "@Symbol";
    };
  }

  private void error(String message,
      javax.lang.model.element.Element element) {
    processingEnv.getMessager().printError(message, element);
    valid = false;
  }
}
