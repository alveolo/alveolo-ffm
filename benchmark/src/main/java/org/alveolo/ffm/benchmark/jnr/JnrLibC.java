package org.alveolo.ffm.benchmark.jnr;

import jnr.ffi.CallingConvention;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;

public interface JnrLibC {
  public static final JnrLibC INSTANCE = LibraryLoader.create(JnrLibC.class)
      .convention(CallingConvention.DEFAULT).searchDefault().load();

  public static final Runtime RUNTIME = Runtime.getRuntime(INSTANCE);

  int abs(int value);
}
