package org.alveolo.ffm.benchmark.callback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;

import org.junit.jupiter.api.Test;

class CallbackTest {
  @Test
  void invokesGeneratedUpcallStub() throws Throwable {
    try (var arena = Arena.ofConfined()) {
      var stub = Add.upcallStub$F(arena, (left, right) -> left + right);
      var invoke = Linker.nativeLinker().downcallHandle(
          stub, Add.FunctionDescriptor$F);

      assertEquals(7, (int) invoke.invokeExact(3, 4));
    }
  }

  @Test
  void rejectsMissingCallbackOrArena() {
    try (var arena = Arena.ofConfined()) {
      assertThrows(NullPointerException.class,
          () -> Add.upcallStub$F(arena, null));
      assertThrows(NullPointerException.class,
          () -> Add.upcallStub$F(null, (left, right) -> 0));
    }
  }
}
