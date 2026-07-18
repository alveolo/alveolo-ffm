package pkg;

import org.alveolo.ffm.*;
import java.lang.foreign.*;

@ForeignInterface
public interface LibC {
  int abs(int number);

  @Symbol("native_call")
  int capturedCall(NativeErrorSpec capture, int parameter);

  @Symbol("captured_div")
  div_t capturedDiv(
      NativeError capture, int numerator, int denominator);

  default int checkedCall(NativeErrorSpec capture, int parameter) {
    int result = capturedCall(capture, parameter);
    capture.throwIf(() -> result == -1);
    return result;
  }

  default int skip() { return abs(0); }
  static int ignore() { return 0; }
  private int helper() { return 0; }

  @Symbol("abs")
  int renamed(int number);

  div_t div(int numerator, int denominator);
  @Value
  ldiv_t ldiv(SegmentAllocator allocator, long numerator, long denominator);

  // TODO support use of size_t
  long strlen(String utf8z);

  String l64a(long n);
}
