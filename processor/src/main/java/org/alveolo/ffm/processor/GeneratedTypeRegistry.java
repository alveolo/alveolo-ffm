package org.alveolo.ffm.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

/// Resolves wrapper class names that are known from source annotations but have
/// not been generated yet.
final class GeneratedTypeRegistry {
  record Wrapper(String className, TypeElement specification) {}

  private final Elements elements;
  private final Types types;
  private final Map<String, List<Wrapper>> byQualifiedName;
  private final Map<String, List<Wrapper>> bySimpleName;

  private GeneratedTypeRegistry(
      Elements elements,
      Types types,
      Map<String, List<Wrapper>> byQualifiedName,
      Map<String, List<Wrapper>> bySimpleName) {
    this.elements = elements;
    this.types = types;
    this.byQualifiedName = byQualifiedName;
    this.bySimpleName = bySimpleName;
  }

  static GeneratedTypeRegistry create(
      ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
    var elements = processingEnv.getElementUtils();
    var qualified = new LinkedHashMap<String, List<Wrapper>>();
    var simple = new LinkedHashMap<String, List<Wrapper>>();

    for (var root : roundEnv.getRootElements()) {
      if (!(root instanceof TypeElement specification)
          || specification.getKind() != ElementKind.INTERFACE
          || specification.getAnnotation(Struct.class) == null
              && specification.getAnnotation(Union.class) == null) {
        continue;
      }

      var className = ProcessorUtils.foreignMemoryClassName(
          specification, elements);
      var wrapper = new Wrapper(className, specification);
      add(qualified, className, wrapper);
      add(simple, simpleName(className), wrapper);
    }

    return new GeneratedTypeRegistry(
        elements, processingEnv.getTypeUtils(), qualified, simple);
  }

  Wrapper find(TypeMirror type, Element useSite) {
    if (type.getKind() != TypeKind.ERROR) return null;

    var typeElement = types.asElement(type);
    if (typeElement instanceof TypeElement errorElement) {
      var qualifiedName = errorElement.getQualifiedName().toString();
      var exact = unique(byQualifiedName.get(qualifiedName));
      if (exact != null) return exact;

      var simpleName = errorElement.getSimpleName().toString();
      var samePackage = findInSamePackage(simpleName, useSite);
      if (samePackage != null) return samePackage;

      var uniqueSimple = unique(bySimpleName.get(simpleName));
      if (uniqueSimple != null) return uniqueSimple;
    }

    var renderedName = type.toString();
    var exact = unique(byQualifiedName.get(renderedName));
    if (exact != null) return exact;

    var simpleName = simpleName(renderedName);
    var samePackage = findInSamePackage(simpleName, useSite);
    return samePackage != null
        ? samePackage : unique(bySimpleName.get(simpleName));
  }

  private Wrapper findInSamePackage(String simpleName, Element useSite) {
    if (useSite == null || simpleName.isEmpty()) return null;

    var packageName = elements.getPackageOf(useSite)
        .getQualifiedName().toString();
    var qualifiedName = ProcessorUtils.qualifyName(packageName, simpleName);
    return unique(byQualifiedName.get(qualifiedName));
  }

  private static void add(
      Map<String, List<Wrapper>> wrappers, String name, Wrapper wrapper) {
    wrappers.computeIfAbsent(name, _ -> new ArrayList<>()).add(wrapper);
  }

  private static Wrapper unique(List<Wrapper> wrappers) {
    return wrappers != null && wrappers.size() == 1
        ? wrappers.getFirst() : null;
  }

  private static String simpleName(String className) {
    var separator = className.lastIndexOf('.');
    return separator < 0 ? className : className.substring(separator + 1);
  }
}
