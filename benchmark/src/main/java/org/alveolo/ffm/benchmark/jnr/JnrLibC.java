package org.alveolo.ffm.benchmark.jnr;

import static jnr.ffi.Platform.getNativePlatform;

import jnr.ffi.CallingConvention;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Platform;
import jnr.ffi.annotations.LongLong;

public interface JnrLibC {
  public static final JnrLibC INSTANCE = LibraryLoader.create(JnrLibC.class)
      .convention(CallingConvention.DEFAULT)
      .load(getNativePlatform().getOS() == Platform.OS.WINDOWS
          ? "ucrtbase" : getNativePlatform().getStandardCLibraryName());

  int abs(int value);

  long labs(long value);

  @LongLong
  long llabs(@LongLong long value);

  long strlen(String str);
}
