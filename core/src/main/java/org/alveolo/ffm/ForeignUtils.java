package org.alveolo.ffm;

import static java.lang.foreign.MemoryLayout.paddingLayout;

import java.lang.foreign.MemoryLayout;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ForeignUtils {
  private ForeignUtils() {/* Utility class */}

  public static MemoryLayout[] pad(MemoryLayout... elements) {
    if (elements.length == 0) return elements;

    var result = new ArrayList<MemoryLayout>(elements.length * 2 - 1);

    long offset = 0;

    for (var element : elements) {
      long byteAligment = element.byteAlignment();

      long misAlignment = offset % byteAligment;
      if (misAlignment != 0) {
        var padding = byteAligment - misAlignment;
        result.add(paddingLayout(padding));
        offset += padding;
      }

      result.add(element);
      offset += element.byteSize();
    }

    // Ensure struct will work well in arrays (SequenceLayout)

    long maxAligment = Stream.of(elements)
        .mapToLong(MemoryLayout::byteAlignment).max().getAsLong();

    long misAlignment = offset % maxAligment;
    if (misAlignment != 0) {
      result.add(paddingLayout(maxAligment - misAlignment));
    }

    return result.toArray(MemoryLayout[]::new);
  }
}
