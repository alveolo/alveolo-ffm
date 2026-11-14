package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.CallbackProcessor")
public final class Tick {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.FunctionDescriptor
      FunctionDescriptor$F = java.lang.foreign.FunctionDescriptor.ofVoid();

  private static final java.lang.invoke.MethodHandle MethodHandle$F =
      methodHandle$F();

  private Tick() {}

  public static java.lang.foreign.MemorySegment upcallStub$F(
      java.lang.foreign.Arena arena, TickSpec callback) {
    java.util.Objects.requireNonNull(callback);
    return Linker$F.upcallStub(
        MethodHandle$F.bindTo(callback), FunctionDescriptor$F, arena);
  }

  private static java.lang.invoke.MethodHandle methodHandle$F() {
    try {
      return java.lang.invoke.MethodHandles.lookup().findVirtual(
          TickSpec.class,
          "invoke",
          FunctionDescriptor$F.toMethodType());
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
