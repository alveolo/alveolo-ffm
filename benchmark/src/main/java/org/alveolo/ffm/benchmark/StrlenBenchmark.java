package org.alveolo.ffm.benchmark;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.concurrent.TimeUnit;

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
public class StrlenBenchmark {
  private static final AffmLibCFFM affm = AffmLibCFFM.INSTANCE;
  private static final JnaLibC jna = JnaLibC.INSTANCE;
  private static final JnrLibC jnr = JnrLibC.INSTANCE;

  private String string = "some arbitrary string to call strlen\0";

  /// 'unfair' baseline - just searches EOL inside the string internal array
  @Benchmark
  public long _java() {
    return string.indexOf(0);
  }

  /// 'fair' baseline that converts string and searches for EOL marker as
  /// reasonable Java String to C char* implementation will do
  @Benchmark
  public long _java_utf8() {
    var bytes = string.getBytes(UTF_8);
    for (int i = 0;; i++) {
      if (bytes[i] == 0) return i;
    }
  }

  @Benchmark
  public long affm() {
    return affm.strlen(string);
  }

  @Benchmark
  public long jna() {
    return jna.strlen(string);
  }

  @Benchmark
  public long jnr() {
    return jnr.strlen(string);
  }
}
