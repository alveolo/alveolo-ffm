package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class VirtualObjFM implements VirtualObj {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.ADDRESS.withName("ff$vtbl"),
        ValueLayout.JAVA_INT.withName("field"),
      }));

  public static final MemoryLayout.PathElement FM$PE$ff$vtbl =
      MemoryLayout.PathElement.groupElement("ff$vtbl");

  public static final MemoryLayout.PathElement FM$PE$field =
      MemoryLayout.PathElement.groupElement("field");

  public static final java.lang.invoke.VarHandle FM$VH$ff$vtbl =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$ff$vtbl), 1, 0L);

  public static final java.lang.invoke.VarHandle FM$VH$field =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$field), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static VirtualObjFM reinterpret(MemorySegment ms) {
    return new VirtualObjFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  private final VirtualObjVtbl ff$vtbl;

  public VirtualObjFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public VirtualObjFM(MemorySegment ms) {
    this.ms = ms;
    this.ff$vtbl = VirtualObjVtblFD.reinterpret((MemorySegment) FM$VH$ff$vtbl.get(ms));
  }

  private VirtualObjVtbl ff$vtbl() {
    return ff$vtbl;
  }

  public int field() {
    return (int) FM$VH$field.get(ms);
  }

  public VirtualObjFM field(int value) {
    FM$VH$field.set(ms, value);
    return this;
  }

  private static final class FF$SYMBOLS {

    private static final MethodHandle FF$MH$0 = pkg.NativeApiFFM.FF$LINKER.downcallHandle(
        pkg.NativeApiFFM.FF$LOOKUP.findOrThrow("native_symbol"),
        FunctionDescriptor.of(
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT));
  }

  public int method(
      int arg) {
    return ff$vtbl().method(this, arg);
  }

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    return ff$vtbl().sum(this, values);
  }

  public int call(
      int arg) {
    try {
      return (int) FF$SYMBOLS.FF$MH$0.invokeExact(
          this.ms,
          arg);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}
