package org.alveolo.ffm.benchmark;

import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibC;
import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.alveolo.ffm.benchmark.jnr.JnrLibC;
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
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DivBenchmark {
  private static final AffmLibC affm = AffmLibCFFM.INSTANCE;
  private static final JnaLibC jna = JnaLibC.INSTANCE;
  private static final JnrLibC jnr = JnrLibC.INSTANCE;

  @Benchmark
  public void op() {
    var quot = 7 / 4;
    var rem = 7 % 4;
    assert quot == 2;
    assert rem == 3;
  }

  @Benchmark
  public void affm_int_struct() {
    var div = affm.div_s(7, 4);
    assert div.quot() == 1;
    assert div.rem() == 3;
  }

  @Benchmark
  public void affm_int_record() {
    var div = affm.div_r(7, 4);
    assert div.quot() == 1;
    assert div.rem() == 3;
  }

  @Benchmark
  public void affm_long_record() {
    var div = affm.ldiv_r(7L, 4L);
    assert div.quot() == 2L;
    assert div.rem() == 3L;
  }

  // @Benchmark
  // public void affm_wrapped() {
  // affm.abs(new IntWrapper(-1));
  // }
  //
  // @Benchmark
  // public void jna() {
  // jna.abs(-1);
  // }
  //
  // @Benchmark
  // public void jnr() {
  // jnr.abs(-1);
  // }
}
