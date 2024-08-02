package org.alveolo.ffm.benchmark;

import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
import org.alveolo.ffm.benchmark.affm.IntWrapper;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.alveolo.ffm.benchmark.jnr.JnrLibC;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class AbsBenchmark {
  private static final AffmLibCFFM affm = AffmLibCFFM.INSTANCE;
  private static final JnaLibC jna = JnaLibC.INSTANCE;
  private static final JnrLibC jnr = JnrLibC.INSTANCE;

  @Benchmark
  public void math() {
    Math.abs(-1);
  }

  @Benchmark
  public void affm() {
    affm.abs(-1);
  }

  @Benchmark
  public void affm_wrapped() {
    affm.abs(new IntWrapper(-1));
  }

  @Benchmark
  public void jna() {
    jna.abs(-1);
  }

  @Benchmark
  public void jnr() {
    jnr.abs(-1);
  }
}
