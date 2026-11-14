package test;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.CallbackProcessor")
public final class Renamed {
  private static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.FunctionDescriptor
      FunctionDescriptor$F = java.lang.foreign.FunctionDescriptor.of(
      java.lang.foreign.ValueLayout.JAVA_LONG,
      java.lang.foreign.ValueLayout.JAVA_LONG);

  private static final java.lang.invoke.MethodHandle MethodHandle$F =
      methodHandle$F();

  private Renamed() {}

  public static java.lang.foreign.MemorySegment upcallStub$F(
      java.lang.foreign.Arena arena, Child callback) {
    java.util.Objects.requireNonNull(callback);
    return Linker$F.upcallStub(
        MethodHandle$F.bindTo(callback), FunctionDescriptor$F, arena);
  }

  private static java.lang.invoke.MethodHandle methodHandle$F() {
    try {
      return java.lang.invoke.MethodHandles.lookup().findVirtual(
          Child.class,
          "invoke",
          FunctionDescriptor$F.toMethodType());
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
