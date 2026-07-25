package org.alveolo.ffm.benchmark;

import java.lang.foreign.Arena;
import java.lang.foreign.SegmentAllocator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibC;
import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
import org.alveolo.ffm.benchmark.affm.div_t_SFM;
import org.alveolo.ffm.benchmark.affm.ldiv_t_SFM;
import org.alveolo.ffm.benchmark.affm.lldiv_t_SFM;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.sun.jna.NativeLong;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DivBenchmark {
  private static final AffmLibC affm = AffmLibCFFM.INSTANCE$F;
  private static final JnaLibC jna = JnaLibC.INSTANCE;

  // JNR does not support struct-by-value passing:
  //
  // @see https://github.com/jnr/jnr-ffi/issues/262
  // private static final JnrLibC jnr = JnrLibC.INSTANCE;

  @State(Scope.Thread)
  static public class DivState {
    Random random = new Random(42);

    int numerator;
    int denominator;
    Arena arena;
    SegmentAllocator allocator;

    @Setup(Level.Trial)
    public void allocate() {
      arena = Arena.ofConfined();
      allocator = SegmentAllocator.prefixAllocator(
          arena.allocate(div_t_SFM.MemoryLayout$F));
    }

    @Setup(Level.Iteration)
    public void prepare() {
      numerator = random.nextInt();
      denominator = (random.nextInt() & 0xFF) + 1;
    }

    @TearDown(Level.Trial)
    public void close() {
      arena.close();
    }
  }

  @State(Scope.Thread)
  static public class LDivState {
    Random random = new Random(422);

    long numerator;
    long denominator;
    NativeLong jnaNumerator;
    NativeLong jnaDenominator;
    Arena arena;
    SegmentAllocator allocator;

    @Setup(Level.Trial)
    public void allocate() {
      arena = Arena.ofConfined();
      allocator = SegmentAllocator.prefixAllocator(
          arena.allocate(ldiv_t_SFM.MemoryLayout$F));
    }

    @Setup(Level.Iteration)
    public void prepare() {
      numerator = random.nextInt();
      denominator = (random.nextInt() & 0xFF) + 1;
      jnaNumerator = new NativeLong(numerator);
      jnaDenominator = new NativeLong(denominator);
    }

    @TearDown(Level.Trial)
    public void close() {
      arena.close();
    }
  }

  @State(Scope.Thread)
  static public class LLDivState {
    Random random = new Random(4222);

    long numerator;
    long denominator;
    Arena arena;
    SegmentAllocator allocator;

    @Setup(Level.Trial)
    public void allocate() {
      arena = Arena.ofConfined();
      allocator = SegmentAllocator.prefixAllocator(
          arena.allocate(lldiv_t_SFM.MemoryLayout$F));
    }

    @Setup(Level.Iteration)
    public void prepare() {
      numerator = random.nextLong();
      denominator = (random.nextInt() & 0xffff_ffffL) + 1;
    }

    @TearDown(Level.Trial)
    public void close() {
      arena.close();
    }
  }

  @Benchmark
  public void _java_div_int(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    bh.consume(numerator / denominator);
    bh.consume(numerator % denominator);
  }

  @Benchmark
  public void _java_div_long(LLDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    bh.consume(numerator / denominator);
    bh.consume(numerator % denominator);
  }

  @Benchmark
  public void affm_div_s(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.div_s(state.allocator, numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_div_r(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.div_r(numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_ldiv_r(LDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.ldiv_r(numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_ldiv_s(LDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.ldiv_s(state.allocator, numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_lldiv_r(LLDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.lldiv_r(numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_lldiv_s(LLDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.lldiv_s(state.allocator, numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void jna_div(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = jna.div(numerator, denominator);
    bh.consume(div.quot);
    bh.consume(div.rem);
  }

  @Benchmark
  public void jna_ldiv(LDivState state, Blackhole bh) {
    var div = jna.ldiv(state.jnaNumerator, state.jnaDenominator);
    bh.consume(div.quot.longValue());
    bh.consume(div.rem.longValue());
  }

  @Benchmark
  public void jna_lldiv(LLDivState state, Blackhole bh) {
    var div = jna.lldiv(state.numerator, state.denominator);
    bh.consume(div.quot);
    bh.consume(div.rem);
  }
}
