package org.alveolo.ffm.benchmark;

import java.lang.foreign.Arena;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmStruct;
import org.alveolo.ffm.benchmark.jna.JnaStruct;
import org.alveolo.ffm.benchmark.jnr.JnrStruct;
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

import com.sun.jna.NativeLong;

import jnr.ffi.Memory;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

/// Compares field access through AFFM, JNA, and JNR struct wrappers.
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 2, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class StructBenchmark {
  private final Random random = new Random(42);

  private Arena arena;
  private AffmStruct affm;
  private JnaStruct jna;
  private JnrStruct jnr;
  private int intValue;
  private long cLongValue;
  private long longValue;
  private NativeLong jnaCLongValue;

  @Setup(Level.Trial)
  public void allocate() {
    arena = Arena.ofConfined();
    affm = new AffmStruct(arena);

    jna = new JnaStruct();
    jna.getPointer();

    var runtime = Runtime.getSystemRuntime();
    jnr = new JnrStruct(runtime);
    jnr.useMemory(Memory.allocateDirect(runtime, Struct.size(jnr)));
  }

  @Setup(Level.Iteration)
  public void prepare() {
    intValue = random.nextInt();
    cLongValue = random.nextInt();
    longValue = random.nextLong();
    jnaCLongValue = new NativeLong(cLongValue);

    affm.i(intValue).cLong(cLongValue).l(longValue);

    jna.writeField("i", intValue);
    jna.writeField("cLong", jnaCLongValue);
    jna.writeField("l", longValue);

    jnr.i.set(intValue);
    jnr.cLong.set(cLongValue);
    jnr.l.set(longValue);
  }

  @TearDown(Level.Trial)
  public void close() {
    arena.close();
  }

  @Benchmark
  public int affm_read_int() {
    return affm.i();
  }

  @Benchmark
  public long affm_read_c_long() {
    return affm.cLong();
  }

  @Benchmark
  public long affm_read_long() {
    return affm.l();
  }

  @Benchmark
  public void affm_write_int() {
    affm.i(intValue);
  }

  @Benchmark
  public void affm_write_c_long() {
    affm.cLong(cLongValue);
  }

  @Benchmark
  public void affm_write_long() {
    affm.l(longValue);
  }

  @Benchmark
  public int jna_read_int() {
    return (int) jna.readField("i");
  }

  @Benchmark
  public long jna_read_c_long() {
    return ((NativeLong) jna.readField("cLong")).longValue();
  }

  @Benchmark
  public long jna_read_long() {
    return (long) jna.readField("l");
  }

  @Benchmark
  public void jna_write_int() {
    jna.writeField("i", intValue);
  }

  @Benchmark
  public void jna_write_c_long() {
    jna.writeField("cLong", jnaCLongValue);
  }

  @Benchmark
  public void jna_write_long() {
    jna.writeField("l", longValue);
  }

  @Benchmark
  public int jnr_read_int() {
    return jnr.i.get();
  }

  @Benchmark
  public long jnr_read_c_long() {
    return jnr.cLong.get();
  }

  @Benchmark
  public long jnr_read_long() {
    return jnr.l.get();
  }

  @Benchmark
  public void jnr_write_int() {
    jnr.i.set(intValue);
  }

  @Benchmark
  public void jnr_write_c_long() {
    jnr.cLong.set(cLongValue);
  }

  @Benchmark
  public void jnr_write_long() {
    jnr.l.set(longValue);
  }
}
