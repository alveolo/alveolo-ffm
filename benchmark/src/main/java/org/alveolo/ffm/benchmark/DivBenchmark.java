package org.alveolo.ffm.benchmark;

import java.lang.foreign.Arena;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibC;
import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DivBenchmark {
  private static final AffmLibC affm = AffmLibCFFM.INSTANCE;
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

    @Setup(Level.Iteration)
    public void prepare() {
      numerator = random.nextInt();
      denominator = (random.nextInt() & 0xFF) + 1;
    }
  }

  @State(Scope.Thread)
  static public class LDivState {
    Random random = new Random(422);

    long numerator;
    long denominator;

    @Setup(Level.Iteration)
    public void prepare() {
      numerator = random.nextLong();
      denominator = (random.nextInt() & 0xFFFFFFFF) + 1;
    }
  }

  @Benchmark
  public void _java_int(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    bh.consume(numerator / denominator);
    bh.consume(numerator % denominator);
  }

  @Benchmark
  public void _java_long(LDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    bh.consume(numerator / denominator);
    bh.consume(numerator % denominator);
  }

  @Benchmark
  public void affm_int_struct(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    try (var arena = Arena.ofConfined()) {
      var div = affm.div_s(arena, numerator, denominator);
      bh.consume(div.quot());
      bh.consume(div.rem());
    }
  }

  @Benchmark
  public void affm_int_record(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.div_r(numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void affm_long_record(LDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = affm.ldiv_r(numerator, denominator);
    bh.consume(div.quot());
    bh.consume(div.rem());
  }

  @Benchmark
  public void jna_int(DivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = jna.div(numerator, denominator);
    bh.consume(div.quot);
    bh.consume(div.rem);
  }

  @Benchmark
  public void jna_long(LDivState state, Blackhole bh) {
    var numerator = state.numerator;
    var denominator = state.denominator;
    var div = jna.ldiv(numerator, denominator);
    bh.consume(div.quot);
    bh.consume(div.rem);
  }
}
