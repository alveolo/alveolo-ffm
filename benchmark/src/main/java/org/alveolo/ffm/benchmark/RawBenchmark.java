package org.alveolo.ffm.benchmark;

import java.lang.foreign.Arena;
import java.lang.foreign.SegmentAllocator;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class RawBenchmark {
  @Benchmark
  public int _arena_confined_alloc8() {
    try (var arena = Arena.ofConfined()) {
      arena.allocate(8);
      return 0;
    }
  }

  @Benchmark
  public int _arena_confined_alloc8x8() {
    try (var arena = Arena.ofConfined()) {
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      arena.allocate(8);
      return 0;
    }
  }

  @Benchmark
  public int _arena_confined_alloc64() {
    try (var arena = Arena.ofConfined()) {
      arena.allocate(64);
      return 0;
    }
  }

  @Benchmark
  public int _arena_confined_alloc64x8() {
    try (var arena = Arena.ofConfined()) {
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      arena.allocate(64);
      return 0;
    }
  }

  @Benchmark
  public int _arena_confined_alloc_mix() {
    try (var arena = Arena.ofConfined()) {
      arena.allocate(64);
      arena.allocate(1);
      arena.allocate(64);
      arena.allocate(1);
      arena.allocate(64);
      arena.allocate(1);
      arena.allocate(64);
      arena.allocate(1);
      return 0;
    }
  }

  @Benchmark
  public int _arena_confined_slice64x8() {
    try (var arena = Arena.ofConfined()) {
      var s = SegmentAllocator.slicingAllocator(arena.allocate(64 * 8));
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      s.allocate(64);
      return 0;
    }
  }
}
