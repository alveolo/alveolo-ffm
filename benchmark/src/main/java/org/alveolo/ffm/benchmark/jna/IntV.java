package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.Structure;

public class IntV extends Structure implements Structure.ByValue {
  public int value;

  public IntV() {}

  public IntV(int value) {
    this.value = value;
  }

  @Override
  protected List<String> getFieldOrder() {
    return List.of("value");
  }
}
