package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class CLongV extends Structure implements Structure.ByValue {
  public NativeLong value;

  public CLongV() {}

  public CLongV(long value) {
    this.value = new NativeLong(value);
  }

  @Override
  protected List<String> getFieldOrder() {
    return List.of("value");
  }
}
