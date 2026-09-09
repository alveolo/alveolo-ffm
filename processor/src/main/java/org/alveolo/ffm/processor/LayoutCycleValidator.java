package org.alveolo.ffm.processor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

/// Checks the actual inline layout dependencies collected across processing
/// rounds. Pointer fields do not depend on their pointee's layout.
final class LayoutCycleValidator {
  record Dependency(String type, Element declaration) {}

  final Map<String, List<Dependency>> dependencies = new LinkedHashMap<>();

  void validate(Messager messager) {
    var visited = new HashSet<String>();
    var active = new HashSet<String>();
    for (var type : dependencies.keySet()) {
      visit(type, visited, active, messager);
    }
  }

  private void visit(String type, Set<String> visited, Set<String> active,
      Messager messager) {
    if (!visited.add(type)) return;
    active.add(type);
    for (var dependency : dependencies.getOrDefault(type, List.of())) {
      if (active.contains(dependency.type())) {
        messager.printError("Recursive inline layout: a struct or union"
            + " cannot contain itself by value", dependency.declaration());
      } else {
        visit(dependency.type(), visited, active, messager);
      }
    }
    active.remove(type);
  }
}
