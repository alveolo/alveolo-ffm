package pkg;


@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class ArrayParametersFFM implements ArrayParameters {
  public static final ArrayParametersFFM INSTANCE = new ArrayParametersFFM();

  private ArrayParametersFFM() {}

  public static final java.lang.foreign.Linker FF$LINKER = java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup FF$LOOKUP =
      FF$LINKER.defaultLookup();

  private static final java.lang.invoke.MethodHandle FF$MH$0 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("scale"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void scale(
      int[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      java.lang.foreign.MemorySegment.copy(values, 0, ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, ff$size$values);
      FF$MH$0.invokeExact(
          ff$ms$values);
      java.lang.foreign.MemorySegment.copy(ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, ff$size$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$1 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("sum"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 3) {
        throw new IllegalArgumentException("values length must be 3");
      }
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      java.lang.foreign.MemorySegment.copy(values, 0, ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, ff$size$values);
      return (int) FF$MH$1.invokeExact(
          ff$ms$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$2 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("fill"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void fill(
      @org.alveolo.ffm.Out @org.alveolo.ffm.Sequence(2L) int[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      if (ff$size$values != 2) {
        throw new IllegalArgumentException("values length must be 2");
      }
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      FF$MH$2.invokeExact(
          ff$ms$values);
      java.lang.foreign.MemorySegment.copy(ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, ff$size$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$3 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("bytes"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void bytes(
      java.nio.ByteBuffer values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$position$values = values.position();
      var ff$size$values = values.remaining();
      if (values.isReadOnly()) {
        throw new IllegalArgumentException(
            "values must be writable unless annotated @In");
      }
      var ff$direct$values = values.isDirect();
      var ff$ms$values = ff$direct$values
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BYTE.byteSize(), (long) ff$size$values))
          : ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_BYTE, ff$size$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          ff$ms$values.setAtIndex(java.lang.foreign.ValueLayout.JAVA_BYTE, ff$i$values,
              values.get(ff$position$values + ff$i$values));
        }
      }
      FF$MH$3.invokeExact(
          ff$ms$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          values.put(ff$position$values + ff$i$values,
              ff$ms$values.getAtIndex(java.lang.foreign.ValueLayout.JAVA_BYTE, ff$i$values));
        }
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$4 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("ints"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void ints(
      java.nio.IntBuffer values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$position$values = values.position();
      var ff$size$values = values.remaining();
      if (ff$size$values != 2) {
        throw new IllegalArgumentException("values remaining must be 2");
      }
      if (values.isReadOnly()) {
        throw new IllegalArgumentException(
            "values must be writable unless annotated @In");
      }
      if (values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var ff$direct$values = values.isDirect();
      var ff$ms$values = ff$direct$values
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) ff$size$values))
          : ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      FF$MH$4.invokeExact(
          ff$ms$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          values.put(ff$position$values + ff$i$values,
              ff$ms$values.getAtIndex(java.lang.foreign.ValueLayout.JAVA_INT, ff$i$values));
        }
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$5 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("flags"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void flags(
      boolean[] values) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$values = values.length;
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_BOOLEAN, ff$size$values);
      for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
        ff$ms$values.setAtIndex(java.lang.foreign.ValueLayout.JAVA_BOOLEAN, ff$i$values,
            values[ff$i$values]);
      }
      FF$MH$5.invokeExact(
          ff$ms$values);
      for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
        values[ff$i$values] = ff$ms$values.getAtIndex(
            java.lang.foreign.ValueLayout.JAVA_BOOLEAN, ff$i$values);
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$6 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("prefix"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public void prefix(
      int[] values,
      int count) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$available$values = values.length;
      var ff$count$values = (long) count;
      if (ff$count$values < 0L || ff$count$values > ff$available$values) {
        throw new IllegalArgumentException(
            "values count parameter 'count' must be between 0 and "
                + ff$available$values + " (length): " + ff$count$values);
      }
      var ff$size$values = (int) ff$count$values;
      var ff$ms$values = ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      java.lang.foreign.MemorySegment.copy(values, 0, ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, ff$size$values);
      FF$MH$6.invokeExact(
          ff$ms$values,
          count);
      java.lang.foreign.MemorySegment.copy(ff$ms$values, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, ff$size$values);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$7 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("transform"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public int transform(
      pkg.CallPoint[] points,
      long count) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$available$points = points.length;
      var ff$count$points = (long) count;
      if (ff$count$points < 0L || ff$count$points > ff$available$points) {
        throw new IllegalArgumentException(
            "points count parameter 'count' must be between 0 and "
                + ff$available$points + " (length): " + ff$count$points);
      }
      var ff$size$points = (int) ff$count$points;
      var ff$ms$points = ff$arena.allocate(
          pkg.CallPointFM.FM$LAYOUT, ff$size$points);
      for (var ff$i$points = 0; ff$i$points < ff$size$points; ff$i$points++) {
        ff$ms$points.asSlice(
            (long) ff$i$points * pkg.CallPointFM.FM$LAYOUT.byteSize(),
            pkg.CallPointFM.FM$LAYOUT).copyFrom(
                pkg.CallPointFM.toMemorySegment(
                    ff$arena, points[ff$i$points]));
      }
      var ff$result = (int) FF$MH$7.invokeExact(
          ff$ms$points,
          count);
      for (var ff$i$points = 0; ff$i$points < ff$size$points; ff$i$points++) {
        points[ff$i$points] = pkg.CallPointFM.fromMemorySegment(
            ff$ms$points.asSlice(
                (long) ff$i$points * pkg.CallPointFM.FM$LAYOUT.byteSize(),
                pkg.CallPointFM.FM$LAYOUT));
      }
      return ff$result;
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$8 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("produce"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void produce(
      pkg.@org.alveolo.ffm.Out CallPoint[] points) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$points = points.length;
      var ff$ms$points = ff$arena.allocate(
          pkg.CallPointFM.FM$LAYOUT, ff$size$points);
      FF$MH$8.invokeExact(
          ff$ms$points);
      for (var ff$i$points = 0; ff$i$points < ff$size$points; ff$i$points++) {
        points[ff$i$points] = pkg.CallPointFM.fromMemorySegment(
            ff$ms$points.asSlice(
                (long) ff$i$points * pkg.CallPointFM.FM$LAYOUT.byteSize(),
                pkg.CallPointFM.FM$LAYOUT));
      }
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$9 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("consume"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void consume(
      pkg.@org.alveolo.ffm.In @org.alveolo.ffm.Sequence(2L) CallPoint[] points) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$size$points = points.length;
      if (ff$size$points != 2) {
        throw new IllegalArgumentException("points length must be 2");
      }
      var ff$ms$points = ff$arena.allocate(
          pkg.CallPointFM.FM$LAYOUT, ff$size$points);
      for (var ff$i$points = 0; ff$i$points < ff$size$points; ff$i$points++) {
        ff$ms$points.asSlice(
            (long) ff$i$points * pkg.CallPointFM.FM$LAYOUT.byteSize(),
            pkg.CallPointFM.FM$LAYOUT).copyFrom(
                pkg.CallPointFM.toMemorySegment(
                    ff$arena, points[ff$i$points]));
      }
      FF$MH$9.invokeExact(
          ff$ms$points);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }

  private static final java.lang.invoke.MethodHandle FF$MH$10 = FF$LINKER.downcallHandle(
      FF$LOOKUP.findOrThrow("readPrefix"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public void readPrefix(
      java.nio.IntBuffer values,
      int count) {
    try (var ff$arena = java.lang.foreign.Arena.ofConfined()) {
      var ff$position$values = values.position();
      var ff$available$values = values.remaining();
      var ff$count$values = (long) count;
      if (ff$count$values < 0L || ff$count$values > ff$available$values) {
        throw new IllegalArgumentException(
            "values count parameter 'count' must be between 0 and "
                + ff$available$values + " (remaining): " + ff$count$values);
      }
      var ff$size$values = (int) ff$count$values;
      if (values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var ff$direct$values = values.isDirect();
      var ff$ms$values = ff$direct$values
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) ff$size$values))
          : ff$arena.allocate(java.lang.foreign.ValueLayout.JAVA_INT, ff$size$values);
      if (!ff$direct$values) {
        for (var ff$i$values = 0; ff$i$values < ff$size$values; ff$i$values++) {
          ff$ms$values.setAtIndex(java.lang.foreign.ValueLayout.JAVA_INT, ff$i$values,
              values.get(ff$position$values + ff$i$values));
        }
      }
      FF$MH$10.invokeExact(
          ff$ms$values,
          count);
    } catch (RuntimeException|Error ff$e) {
      throw ff$e;
    } catch (Throwable ff$t) {
      throw new AssertionError(ff$t);
    }
  }
}
