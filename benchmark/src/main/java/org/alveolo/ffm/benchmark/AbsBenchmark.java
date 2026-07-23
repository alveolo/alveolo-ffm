package org.alveolo.ffm.benchmark;

import java.lang.foreign.Arena;
import java.lang.foreign.SegmentAllocator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.alveolo.ffm.benchmark.affm.AffmLibCFFM;
import org.alveolo.ffm.benchmark.affm.CLongR;
import org.alveolo.ffm.benchmark.affm.CLongSFM;
import org.alveolo.ffm.benchmark.affm.IntR;
import org.alveolo.ffm.benchmark.affm.IntSFM;
import org.alveolo.ffm.benchmark.affm.LongR;
import org.alveolo.ffm.benchmark.affm.LongSFM;
import org.alveolo.ffm.benchmark.jna.CLongV;
import org.alveolo.ffm.benchmark.jna.IntV;
import org.alveolo.ffm.benchmark.jna.JnaLibC;
import org.alveolo.ffm.benchmark.jna.LongV;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.sun.jna.NativeLong;

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

  private Random random = new Random(42);

  private Arena arena;
  private IntSFM intS;
  private CLongSFM cLongS;
  private LongSFM longS;
  private SegmentAllocator intSAllocator;
  private SegmentAllocator cLongSAllocator;
  private SegmentAllocator longSAllocator;
  private int value;
  private NativeLong jnaValue;

  @Setup(Level.Trial)
  public void allocate() {
    arena = Arena.ofConfined();
    intS = new IntSFM(arena);
    cLongS = new CLongSFM(arena);
    longS = new LongSFM(arena);
    intSAllocator = SegmentAllocator.prefixAllocator(
        arena.allocate(IntSFM.MemoryLayout$F));
    cLongSAllocator = SegmentAllocator.prefixAllocator(
        arena.allocate(CLongSFM.MemoryLayout$F));
    longSAllocator = SegmentAllocator.prefixAllocator(
        arena.allocate(LongSFM.MemoryLayout$F));
  }

  @Setup(Level.Iteration)
  public void prepare() {
    do {
      value = random.nextInt();
    } while (value == Integer.MIN_VALUE);
    jnaValue = new NativeLong(value);
    intS.value(value);
    cLongS.value(value);
    longS.value(value);
  }

  @TearDown(Level.Trial)
  public void close() {
    arena.close();
  }

  @Benchmark
  public int _math_abs_int() {
    return Math.abs(value);
  }

  @Benchmark
  public long _math_abs_long() {
    return Math.abs((long) value);
  }

  @Benchmark
  public int affm_abs() {
    return affm.abs(value);
  }

  @Benchmark
  public IntR affm_abs_r() {
    return affm.abs(new IntR(value));
  }

  @Benchmark
  public int affm_abs_s() {
    return affm.abs(intSAllocator, intS).value();
  }

  @Benchmark
  public long affm_labs() {
    return affm.labs(value);
  }

  @Benchmark
  public CLongR affm_labs_r() {
    return affm.labs(new CLongR(value));
  }

  @Benchmark
  public long affm_labs_s() {
    return affm.labs(cLongSAllocator, cLongS).value();
  }

  @Benchmark
  public long affm_llabs() {
    return affm.llabs(value);
  }

  @Benchmark
  public LongR affm_llabs_r() {
    return affm.llabs(new LongR(value));
  }

  @Benchmark
  public long affm_llabs_s() {
    return affm.llabs(longSAllocator, longS).value();
  }

  @Benchmark
  public int jna_abs() {
    return jna.abs(value);
  }

  @Benchmark
  public IntV jna_abs_s() {
    return jna.abs(new IntV(value));
  }

  @Benchmark
  public NativeLong jna_labs() {
    return jna.labs(jnaValue);
  }

  @Benchmark
  public CLongV jna_labs_s() {
    return jna.labs(new CLongV(value));
  }

  @Benchmark
  public long jna_llabs() {
    return jna.llabs(value);
  }

  @Benchmark
  public LongV jna_llabs_s() {
    return jna.llabs(new LongV(value));
  }

  @Benchmark
  public int jnr_abs() {
    return jnr.abs(value);
  }

  @Benchmark
  public long jnr_labs() {
    return jnr.labs(value);
  }

  @Benchmark
  public long jnr_llabs() {
    return jnr.llabs(value);
  }
}
