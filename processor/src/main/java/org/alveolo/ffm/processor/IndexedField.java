package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.IntStream;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/// Immutable description of an inline C array field.
///
/// The element generator deliberately describes only one element. Dimensions and
/// the Java carrier used by record snapshots are kept separately so that a field
/// layout is never confused with a call-side pointer carrier.
record IndexedField(
    VariableGenerator element,
    TypeMirror declaredType,
    List<Dimension> dimensions,
    Element declaration,
    boolean recordSnapshot
) {
  record Dimension(String name, String typeName, long size) {}

  IndexedField {
    if (dimensions.isEmpty())
      throw new IllegalArgumentException("Indexed fields need a dimension");
  }

  String declaredTypeName() {
    return recordSnapshot ? element.typeName() + "[]" : declaredType.toString();
  }

  String layout() {
    var layout = element.layout();
    for (var i = dimensions.size() - 1; i >= 0; i--) {
      layout = """
          java.lang.foreign.MemoryLayout.sequenceLayout(<size>L,
              <layout>)
          """
          .replace("<size>", Long.toString(dimensions.get(i).size()))
          .replace("<layout>", layout.replace("\n", "\n    "))
          .strip();
    }
    return layout;
  }

  boolean oneDimensional() {
    return dimensions.size() == 1;
  }

  String accessorParameterDeclarations() {
    return IntStream.range(0, dimensions.size())
        .mapToObj(i -> dimensions.get(i).typeName() + " index" + i + "$f")
        .collect(joining(", "));
  }

  String accessorParameterNames() {
    return IntStream.range(0, dimensions.size())
        .mapToObj(i -> "index" + i + "$f")
        .collect(joining(", "));
  }

  String helperParameterDeclarations() {
    return IntStream.range(0, dimensions.size())
        .mapToObj(i -> dimensions.get(i).typeName() + " index" + i)
        .collect(joining(", "));
  }

  String helperParameterNames() {
    return IntStream.range(0, dimensions.size())
        .mapToObj(i -> "index" + i)
        .collect(joining(", "));
  }

  boolean primitive() {
    return element.isPrimitive() && !element.isPrimitiveAddress();
  }

  boolean addressElement() {
    return element.isMemorySegment()
        || (element.isForeignMemory() && element.isAddress());
  }

  boolean structuredValueElement() {
    return element.isForeignMemory() && element.isValue();
  }
}
