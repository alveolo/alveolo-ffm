package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class ObjFM implements Obj {
  public static final MemoryLayout FM$LAYOUT =
      MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {
        ValueLayout.JAVA_INT.withName("field"),
      }));

  public static final MemoryLayout.PathElement FM$PE$field =
      MemoryLayout.PathElement.groupElement("field");

  public static final java.lang.invoke.VarHandle FM$VH$field =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$field), 1, 0L);

  public static MemorySegment allocate(SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static ObjFM reinterpret(MemorySegment ms) {
    return new ObjFM(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public final MemorySegment ms;

  public ObjFM(SegmentAllocator allocator) {
    this(allocate(allocator));
  }

  public ObjFM(MemorySegment ms) {
    this.ms = ms;
  }

  public int field() {
    return (int) FM$VH$field.get(ms);
  }

  public ObjFM field(int value) {
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

    private static final MethodHandle FF$MH$1 = pkg.NativeApiFFM.FF$LINKER.downcallHandle(
        pkg.NativeApiFFM.FF$LOOKUP.findOrThrow("native_strlen"),
        FunctionDescriptor.of(
            ValueLayout.JAVA_LONG,
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS));
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

  public long strlen(
      java.lang.String value) {
    try (var ff$arena = Arena.ofConfined()) {
      return (long) FF$SYMBOLS.FF$MH$1.invokeExact(
          this.ms,
          ff$arena.allocateFrom(value));
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}
