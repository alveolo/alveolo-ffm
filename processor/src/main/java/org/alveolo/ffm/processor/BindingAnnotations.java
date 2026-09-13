package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import org.alveolo.ffm.Release;
import org.alveolo.ffm.Require;
import org.alveolo.ffm.macos.CFString;

/// Fixed binding annotation bundles, interpreted without loading user classes.
/// Expansion preserves type positions; callers decide which positions apply.
record BindingAnnotations(
    boolean cfString,
    String release,
    List<Requirement> requirements,
    List<String> errors
) {
  record Requirement(TypeMirror type, String origin) {}

  private static final String CF_STRING = CFString.class.getCanonicalName();
  private static final String RELEASE = Release.class.getCanonicalName();
  private static final String REQUIRE = Require.class.getCanonicalName();

  private static final Set<String> LEAVES = Set.of(CF_STRING, RELEASE, REQUIRE);

  static BindingAnnotations read(
      ProcessingEnvironment env, AnnotatedConstruct... sites) {
    var reader = new Reader(env.getElementUtils());
    for (var site : sites) {
      if (site != null) {
        for (var annotation : site.getAnnotationMirrors()) {
          reader.expand(annotation, new ArrayList<>());
        }
      }
    }
    return new BindingAnnotations(reader.cfString, reader.release,
        List.copyOf(reader.requirements.values()), List.copyOf(reader.errors));
  }

  static boolean participates(ProcessingEnvironment env,
      AnnotationMirror annotation) {
    return new Reader(env.getElementUtils()).participates(
        (TypeElement) annotation.getAnnotationType().asElement(),
        new HashSet<>());
  }

  List<String> source(Elements elements) {
    var result = new ArrayList<String>();
    if (cfString) {
      result.add("@" + CF_STRING);
    }
    if (release != null) {
      result.add("@" + RELEASE
          + "(" + elements.getConstantExpression(release) + ")");
    }
    if (!requirements.isEmpty()) {
      result.add("@" + REQUIRE + "({" + requirements.stream()
          .map(requirement -> requirement.type() + ".class")
          .collect(joining(", ")) + "})");
    }
    return result;
  }

  boolean validate(ProcessingEnvironment env,
      TypeElement binding, Element useSite) {
    var valid = errors.isEmpty();
    for (var error : errors) {
      env.getMessager().printError(error, useSite);
    }
    var types = env.getTypeUtils();
    for (var requirement : requirements) {
      var required = types.asElement(requirement.type());
      if (!(required instanceof TypeElement iface)
          || iface.getKind() != ElementKind.INTERFACE) {
        env.getMessager().printError(
            "@Require must name an interface: " + requirement.type(), useSite);
        valid = false;
      } else if (!types.isSubtype(types.erasure(binding.asType()),
          types.erasure(requirement.type()))) {
        env.getMessager().printError(requirement.origin() + " requires "
            + iface.getQualifiedName() + "; " + binding.getQualifiedName()
            + " does not extend it", useSite);
        valid = false;
      }
    }
    return valid;
  }

  static boolean validateBinding(ProcessingEnvironment env,
      TypeElement binding) {
    return validateBinding(env, binding, binding, new HashSet<>());
  }

  private static boolean validateBinding(ProcessingEnvironment env,
      TypeElement binding, TypeElement type, Set<String> visited) {
    if (!visited.add(type.getQualifiedName().toString()))
      return true;
    var annotations = read(env, type);
    var valid = annotations.validate(env, binding, type);
    if (annotations.cfString || annotations.release != null) {
      env.getMessager().printError(
          "@CFString and @Release are not supported on binding declarations",
          type);
      valid = false;
    }
    for (var parent : type.getInterfaces()) {
      valid &= validateBinding(env, binding,
          (TypeElement) env.getTypeUtils().asElement(parent), visited);
    }
    return valid;
  }

  static boolean validateNested(ProcessingEnvironment env, TypeMirror type,
      TypeElement binding, Element useSite) {
    var nested =
        type instanceof ArrayType array ? List.of(array.getComponentType())
            : type instanceof DeclaredType declared
                ? declared.getTypeArguments()
            : List.<TypeMirror> of();
    var valid = true;
    for (var component : nested) {
      var annotations = read(env, component);
      valid &= annotations.validate(env, binding, useSite);
      if (annotations.release != null || annotations.cfString) {
        env.getMessager().printError(
            "@Release and @CFString are not supported on nested type positions",
            useSite);
        valid = false;
      }
      valid &= validateNested(env, component, binding, useSite);
    }
    return valid;
  }

  private static final class Reader {
    final Elements elements;
    final LinkedHashMap<String, Requirement> requirements =
        new LinkedHashMap<>();
    final Set<String> errors = new LinkedHashSet<>();
    boolean cfString;
    String release;
    String releaseOrigin;

    Reader(Elements elements) {
      this.elements = elements;
    }

    private boolean participates(TypeElement type, Set<String> visited) {
      var name = type.getQualifiedName().toString();
      if (name.startsWith("java.lang.annotation.") || !visited.add(name))
        return false;
      if (LEAVES.contains(name)) return true;
      return type.getAnnotationMirrors().stream()
          .anyMatch(annotation -> participates(
              (TypeElement) annotation.getAnnotationType().asElement(),
              visited));
    }

    void expand(AnnotationMirror annotation, List<String> path) {
      var type = (TypeElement) annotation.getAnnotationType().asElement();
      if (!participates(type, new HashSet<>())) return;
      var name = type.getQualifiedName().toString();
      if (path.contains(name)) {
        errors.add("Cyclic binding annotation composition: "
            + String.join(" -> ", path) + " -> " + name);
        return;
      }
      path.add(name);
      var origin = "@" + String.join(" -> @", path);
      var retention = type.getAnnotation(Retention.class);
      if (retention != null && retention.value() == RetentionPolicy.SOURCE) {
        errors.add("Composed binding annotations require CLASS or RUNTIME "
            + "retention: " + name);
      }
      if (!LEAVES.contains(name)
          && !ElementFilter.methodsIn(type.getEnclosedElements()).isEmpty()) {
        errors.add("Composed binding annotations must not declare elements: "
            + name);
      }
      if (name.equals(CF_STRING)) {
        cfString = true;
      } else if (name.equals(RELEASE)) {
        var value = (String) value(annotation).getValue();
        if (release != null && !release.equals(value)) {
          errors.add("Conflicting @Release methods: " + release + " through "
              + releaseOrigin + "; " + value + " through " + origin);
        } else {
          release = value;
          releaseOrigin = origin;
        }
      } else if (name.equals(REQUIRE)) {
        @SuppressWarnings("unchecked")
        var values =
            (List<? extends AnnotationValue>) value(annotation).getValue();
        for (var value : values) {
          var required = (TypeMirror) value.getValue();
          requirements.putIfAbsent(required.toString(),
              new Requirement(required, origin));
        }
      }
      // Recognized annotations can themselves carry requirements or bundles.
      for (var meta : type.getAnnotationMirrors()) {
        expand(meta, path);
      }
      path.removeLast();
    }

    private AnnotationValue value(AnnotationMirror annotation) {
      return elements.getElementValuesWithDefaults(annotation)
          .entrySet().stream()
          .filter(entry -> entry.getKey()
              .getSimpleName().contentEquals("value"))
          .findFirst().orElseThrow().getValue();
    }
  }
}
