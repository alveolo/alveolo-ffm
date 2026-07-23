package org.alveolo.ffm.benchmark.jnr;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class JnrStruct extends Struct {
  public final Signed32 i = new Signed32();
  public final SignedLong cLong = new SignedLong();
  public final Signed64 l = new Signed64();

  public JnrStruct(Runtime runtime) {
    super(runtime);
  }
}
