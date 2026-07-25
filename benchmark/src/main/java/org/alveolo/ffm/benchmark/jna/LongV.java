package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.Structure;

public class LongV extends Structure implements Structure.ByValue {
  public long value;

  public LongV() {}

  public LongV(long value) {
    this.value = value;
  }

  @Override
  protected List<String> getFieldOrder() {
    return List.of("value");
  }
}
