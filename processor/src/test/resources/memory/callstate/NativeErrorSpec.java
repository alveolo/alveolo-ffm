package pkg;

import java.util.function.BooleanSupplier;
import java.util.function.IntPredicate;

import org.alveolo.ffm.CallState;
import org.alveolo.ffm.Library;

@CallState(
    value = "errno",
    overrides = @CallState.Override(
        os = Library.OS.WINDOWS,
        value = "GetLastError"))
public interface NativeErrorSpec {
  int error();

  default void throwIf(BooleanSupplier failure) {
    int error = error();
    if (failure.getAsBoolean()) throw new IllegalStateException(
        "native error: " + error);
  }

  default void throwIf(IntPredicate failure) {
    int error = error();
    if (failure.test(error)) throw new IllegalStateException(
        "native error: " + error);
  }
}
