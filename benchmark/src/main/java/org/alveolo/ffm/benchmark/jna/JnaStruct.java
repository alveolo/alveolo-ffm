package org.alveolo.ffm.benchmark.jna;

import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class JnaStruct extends Structure {
  public int i;
  public NativeLong cLong = new NativeLong();
  public long l;

  @Override
  protected List<String> getFieldOrder() {
    return List.of("i", "cLong", "l");
  }
}
