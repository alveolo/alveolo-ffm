package org.alveolo.ffm.benchmark.nativecall;

import java.util.function.BooleanSupplier;

import org.alveolo.ffm.CallState;

@CallState("errno")
public interface ErrnoSpec {
  int errno();

  default void throwIf(BooleanSupplier failure) {
    int errno = errno();
    if (failure.getAsBoolean()) {
      throw new IllegalStateException("native error: " + errno);
    }
  }
}
