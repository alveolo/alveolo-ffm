package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.CallbackProcessor")
public final class Event {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.FunctionDescriptor
      FunctionDescriptor$F = java.lang.foreign.FunctionDescriptor.ofVoid(
      java.lang.foreign.ValueLayout.ADDRESS,
      java.lang.foreign.ValueLayout.JAVA_INT,
      java.lang.foreign.ValueLayout.ADDRESS);

  private static final java.lang.invoke.MethodHandle MethodHandle$F =
      methodHandle$F();

  private Event() {}

  public static java.lang.foreign.MemorySegment upcallStub$F(
      java.lang.foreign.Arena arena, EventSpec callback) {
    java.util.Objects.requireNonNull(callback);
    return Linker$F.upcallStub(
        MethodHandle$F.bindTo(callback), FunctionDescriptor$F, arena);
  }

  private static java.lang.invoke.MethodHandle methodHandle$F() {
    try {
      return java.lang.invoke.MethodHandles.lookup().findVirtual(
          EventSpec.class,
          "invoke",
          FunctionDescriptor$F.toMethodType());
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
