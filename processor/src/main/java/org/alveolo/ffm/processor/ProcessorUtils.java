package org.alveolo.ffm.processor;

import javax.lang.model.element.TypeElement;

import org.alveolo.ffm.ForeignName;

public class ProcessorUtils {
  static String foreignClassName(TypeElement element) {
    var foreignName = element.getAnnotation(ForeignName.class);
    if (foreignName != null) {
      String value = foreignName.value();
      if (value.length() > 0) return foreignName.value();
    }

    return element.getQualifiedName() + "FM";
  }
}
