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
import org.openjdk.jmh.infra.Blackhole;

/// Measures arena allocation and slicing, consuming each segment before closing.
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class RawBenchmark {
  @Benchmark
  public void _arena_confined_alloc8(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      blackhole.consume(arena.allocate(8));
    }
  }

  @Benchmark
  public void _arena_confined_alloc8x8(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
      blackhole.consume(arena.allocate(8));
    }
  }

  @Benchmark
  public void _arena_confined_alloc64(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      blackhole.consume(arena.allocate(64));
    }
  }

  @Benchmark
  public void _arena_confined_alloc64x8(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(64));
    }
  }

  @Benchmark
  public void _arena_confined_alloc_mix(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(1));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(1));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(1));
      blackhole.consume(arena.allocate(64));
      blackhole.consume(arena.allocate(1));
    }
  }

  @Benchmark
  public void _arena_confined_slice64x8(Blackhole blackhole) {
    try (var arena = Arena.ofConfined()) {
      var s = SegmentAllocator.slicingAllocator(arena.allocate(64 * 8));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
      blackhole.consume(s.allocate(64));
    }
  }
}
