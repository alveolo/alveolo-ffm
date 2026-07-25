package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class ldiv_t extends Structure implements Structure.ByValue {
  public NativeLong quot;
  public NativeLong rem;

  @Override
  protected List<String> getFieldOrder() {
    return List.of("quot", "rem");
  }
}
