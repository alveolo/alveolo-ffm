package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignMemoryProcessor")
public final class div_tFM {
  public static final java.lang.foreign.MemoryLayout FM$LAYOUT =
      java.lang.foreign.MemoryLayout.structLayout(
          org.alveolo.ffm.ForeignUtils.structPad(new java.lang.foreign.MemoryLayout [] {
        java.lang.foreign.ValueLayout.JAVA_INT.withName("quot"),
        java.lang.foreign.ValueLayout.JAVA_INT.withName("rem"),
      }));

  public static java.lang.foreign.MemorySegment allocate(java.lang.foreign.SegmentAllocator allocator) {
    return allocator.allocate(
      FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment());
  }

  public static java.lang.foreign.MemorySegment allocate(
      java.lang.foreign.SegmentAllocator allocator, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return allocator.allocate(FM$LAYOUT, count);
  }

  public static div_t reinterpret(java.lang.foreign.MemorySegment ms) {
    return fromMemorySegment(ms.reinterpret(FM$LAYOUT.byteSize()));
  }

  public static java.lang.foreign.MemorySegment reinterpret(
      java.lang.foreign.MemorySegment ms, long count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be non-negative");
    }
    return ms.reinterpret(Math.multiplyExact(
        FM$LAYOUT.byteSize(), count));
  }

  private static java.lang.foreign.MemorySegment FM$at(java.lang.foreign.MemorySegment array, long index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException(index);
    }
    return array.asSlice(Math.multiplyExact(
        index, FM$LAYOUT.byteSize()), FM$LAYOUT.byteSize());
  }

  public static div_t at(java.lang.foreign.MemorySegment array, long index) {
    return fromMemorySegment(FM$at(array, index));
  }

  public static void toMemorySegment(div_t from, java.lang.foreign.MemorySegment ms) {
    quot(ms, from.quot());
    rem(ms, from.rem());
  }

  public static java.lang.foreign.MemorySegment toMemorySegment(
      java.lang.foreign.SegmentAllocator allocator, div_t from) {
    var ms = allocate(allocator);
    toMemorySegment(from, ms);
    return ms;
  }

  public static div_t fromMemorySegment(java.lang.foreign.MemorySegment ms) {
    return new div_t(
        quot(ms),
        rem(ms));
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$quot =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("quot");

  public static final java.lang.invoke.VarHandle FM$VH$quot =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$quot), 1, 0L);

  public static int quot(java.lang.foreign.MemorySegment ms) {
    return (int) FM$VH$quot.get(ms);
  }

  public static void quot(java.lang.foreign.MemorySegment ms, int value) {
    FM$VH$quot.set(ms, value);
  }

  public static final java.lang.foreign.MemoryLayout.PathElement FM$PE$rem =
      java.lang.foreign.MemoryLayout.PathElement.groupElement("rem");

  public static final java.lang.invoke.VarHandle FM$VH$rem =
      java.lang.invoke.MethodHandles.insertCoordinates(
          FM$LAYOUT.varHandle(FM$PE$rem), 1, 0L);

  public static int rem(java.lang.foreign.MemorySegment ms) {
    return (int) FM$VH$rem.get(ms);
  }

  public static void rem(java.lang.foreign.MemorySegment ms, int value) {
    FM$VH$rem.set(ms, value);
  }
}
