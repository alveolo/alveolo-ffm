package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.Structure;

public class ldiv_t extends Structure implements Structure.ByValue {
  public int quot;
  public int rem;

  @Override
  protected List<String> getFieldOrder() {
    return List.of("quot", "rem");
  }
}
