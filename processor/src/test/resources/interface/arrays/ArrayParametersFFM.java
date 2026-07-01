package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class ArrayParametersFFM implements ArrayParameters {
  public static final ArrayParametersFFM INSTANCE = new ArrayParametersFFM();

  private ArrayParametersFFM() {}

  public static final Linker FF$LINKER = Linker.nativeLinker();

  public static final SymbolLookup FF$LOOKUP =
      FF$LINKER.defaultLookup();

  private static final MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("scale"),
      FunctionDescriptor.ofVoid(
          ValueLayout.ADDRESS));

  public void scale(
      int[] values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$size$values = values.length;
      var ff$ms$values = ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      MemorySegment.copy(values, 0, ff$ms$values, ValueLayout.JAVA_INT, 0, ff$size$values);
      FF$MH$0.invokeExact(
          ff$ms$values);
      MemorySegment.copy(ff$ms$values, ValueLayout.JAVA_INT, 0, values, 0, ff$size$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("sum"),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.ADDRESS));

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      MemorySegment.copy(values, 0, ff$ms$values, ValueLayout.JAVA_INT, 0, ff$size$values);
      return (int) FF$MH$1.invokeExact(
          ff$ms$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("fill"),
      FunctionDescriptor.ofVoid(
          ValueLayout.ADDRESS));

  public void fill(
      @org.alveolo.ffm.Out @org.alveolo.ffm.Sequence(2L) int[] values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 2) {
        throw new IllegalArgumentException("values length must be 2");
      }
      var ff$ms$values = ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      FF$MH$2.invokeExact(
          ff$ms$values);
      MemorySegment.copy(ff$ms$values, ValueLayout.JAVA_INT, 0, values, 0, ff$size$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("bytes"),
      FunctionDescriptor.ofVoid(
          ValueLayout.ADDRESS));

  public void bytes(
      java.nio.ByteBuffer values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$position$values = values.position();
      var ff$size$values = values.remaining();
      var ff$direct$values = values.isDirect();
      var ff$ms$values = ff$direct$values
          ? MemorySegment.ofBuffer(values)
          : ff$arena.allocate(ValueLayout.JAVA_BYTE, ff$size$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          ff$ms$values.setAtIndex(ValueLayout.JAVA_BYTE, ff$i$values,
              values.get(ff$position$values + ff$i$values));
        }
      }
      FF$MH$3.invokeExact(
          ff$ms$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          values.put(ff$position$values + ff$i$values,
              ff$ms$values.getAtIndex(ValueLayout.JAVA_BYTE, ff$i$values));
        }
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("ints"),
      FunctionDescriptor.ofVoid(
          ValueLayout.ADDRESS));

  public void ints(
      java.nio.IntBuffer values) {
    try (var ff$arena = Arena.ofConfined()) {
      var ff$position$values = values.position();
      var ff$size$values = values.remaining();
      if (ff$size$values != 2) {
        throw new IllegalArgumentException("values remaining must be 2");
      }
      var ff$direct$values = values.isDirect();
      var ff$ms$values = ff$direct$values
          ? MemorySegment.ofBuffer(values)
          : ff$arena.allocate(ValueLayout.JAVA_INT, ff$size$values);
      FF$MH$4.invokeExact(
          ff$ms$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          values.put(ff$position$values + ff$i$values,
              ff$ms$values.getAtIndex(ValueLayout.JAVA_INT, ff$i$values));
        }
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}
