package org.alveolo.ffm.processor;

import javax.lang.model.element.Element;

/// Throw in high-level validations happening before target file is created.
///
/// Otherwise ensure it is caught in respective local scope to avoid generating
/// more errors in generated files than necessary. Replace respective target
/// source fragments with placeholders so that only actual cause of the error
/// is reported.
@SuppressWarnings("serial")
public class ProcessorError extends Exception {
  private final Element element;

  public ProcessorError(Element element, String message) {
    super(message);
    this.element = element;
  }

  public Element getElement() {
    return element;
  }
}
