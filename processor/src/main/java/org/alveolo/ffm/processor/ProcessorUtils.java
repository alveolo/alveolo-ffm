package org.alveolo.ffm.processor;

import javax.lang.model.element.TypeElement;

import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Union;

public class ProcessorUtils {
  private ProcessorUtils() {}

  static String foreignClassName(TypeElement element) {
    var struct = element.getAnnotation(Struct.class);
    if (struct != null && !struct.name().isEmpty())
      return struct.name();

    var union = element.getAnnotation(Union.class);
    if (union != null && !union.name().isEmpty())
      return union.name();

    var name = element.getAnnotation(Symbol.class);
    if (name != null && !name.value().isEmpty())
      return name.value();

    // TODO the above assumes simple name and below qualified name
    // make consistent (caller expect simple name and adding package themselves

    return element.getQualifiedName() + "FM";
  }

  static String dispatchTableClassName(TypeElement element) {
    var dispatchTable = element.getAnnotation(DispatchTable.class);
    if (dispatchTable != null && !dispatchTable.name().isEmpty())
      return dispatchTable.name();

    return element.getQualifiedName() + "FD";
  }
}
