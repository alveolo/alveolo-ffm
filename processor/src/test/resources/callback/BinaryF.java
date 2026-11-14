package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.CallbackProcessor")
public final class BinaryF {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.FunctionDescriptor
      FunctionDescriptor$F = java.lang.foreign.FunctionDescriptor.of(
      java.lang.foreign.ValueLayout.JAVA_INT,
      java.lang.foreign.ValueLayout.JAVA_INT,
      java.lang.foreign.ValueLayout.JAVA_INT);

  private static final java.lang.invoke.MethodHandle MethodHandle$F =
      methodHandle$F();

  private BinaryF() {}

  public static java.lang.foreign.MemorySegment upcallStub$F(
      java.lang.foreign.Arena arena, Binary callback) {
    java.util.Objects.requireNonNull(callback);
    return Linker$F.upcallStub(
        MethodHandle$F.bindTo(callback), FunctionDescriptor$F, arena);
  }

  private static java.lang.invoke.MethodHandle methodHandle$F() {
    try {
      return java.lang.invoke.MethodHandles.lookup().findVirtual(
          Binary.class,
          "apply",
          FunctionDescriptor$F.toMethodType());
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
