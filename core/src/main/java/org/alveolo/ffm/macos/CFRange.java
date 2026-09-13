package org.alveolo.ffm.macos;

import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;

import org.alveolo.ffm.Value;

/// A memory-backed CoreFoundation range of two 64-bit CFIndex values.
///
/// Passed by value by default. Use [org.alveolo.ffm.Address] for pointer
/// parameters, such as output ranges. The caller owns the backing segment
/// and its lifetime.
@Value
public final class CFRange {
  public static final StructLayout MemoryLayout$F = MemoryLayout.structLayout(
      JAVA_LONG.withName("location"), JAVA_LONG.withName("length"));

  public final MemorySegment MemorySegment$F;

  public CFRange(SegmentAllocator allocator) {
    this(allocator.allocate(MemoryLayout$F));
  }

  public CFRange(MemorySegment memorySegment) {
    MemorySegment$F = memorySegment;
  }

  public long location() {
    return MemorySegment$F.get(JAVA_LONG, 0);
  }

  public CFRange location(long value) {
    MemorySegment$F.set(JAVA_LONG, 0, value);
    return this;
  }

  public long length() {
    return MemorySegment$F.get(JAVA_LONG, JAVA_LONG.byteSize());
  }

  public CFRange length(long value) {
    MemorySegment$F.set(JAVA_LONG, JAVA_LONG.byteSize(), value);
    return this;
  }
}
