package org.alveolo.ffm.benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
import org.alveolo.ffm.benchmark.affm.IntWrapper;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.alveolo.ffm.benchmark.jnr.JnrLibC;
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

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class AbsBenchmark {
  private static final AffmLibCFFM affm = AffmLibCFFM.INSTANCE$F;
  private static final JnaLibC jna = JnaLibC.INSTANCE;
  private static final JnrLibC jnr = JnrLibC.INSTANCE;

  Random random = new Random(42);

  private int value;

  @Setup(Level.Iteration)
  public void prepare() {
    value = random.nextInt();
  }

  @Benchmark
  public int _math() {
    return Math.abs(value);
  }

  @Benchmark
  public int affm() {
    return affm.abs(value);
  }

  @Benchmark
  public IntWrapper affm_wrapped() {
    return affm.abs(new IntWrapper(value));
  }

  @Benchmark
  public int jna() {
    return jna.abs(value);
  }

  @Benchmark
  public int jnr() {
    return jnr.abs(value);
  }
}
