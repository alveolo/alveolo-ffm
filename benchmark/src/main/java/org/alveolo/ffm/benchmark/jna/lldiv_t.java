package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.Structure;

public class lldiv_t extends Structure implements Structure.ByValue {
  public long quot;
  public long rem;

  @Override
  protected List<String> getFieldOrder() {
    return List.of("quot", "rem");
  }
}
