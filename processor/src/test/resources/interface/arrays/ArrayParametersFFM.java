package pkg;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class ArrayParametersFFM implements ArrayParameters {
  public static final ArrayParametersFFM INSTANCE$F = new ArrayParametersFFM();

  private ArrayParametersFFM() {}

  public static final java.lang.foreign.Linker Linker$F =
      java.lang.foreign.Linker.nativeLinker();

  public static final java.lang.foreign.SymbolLookup
      SymbolLookup$F = Linker$F.defaultLookup();

  private static final java.lang.invoke.MethodHandle MethodHandle$0$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("scale"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void scale(
      int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      MethodHandle$0$F.invokeExact(
          values$MemorySegment$f);
      java.lang.foreign.MemorySegment.copy(
          values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("sum"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS));

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      return (int) MethodHandle$1$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("fill"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void fill(
      @org.alveolo.ffm.Out @org.alveolo.ffm.Sequence(2L) int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 2) {
        throw new IllegalArgumentException(
            "values length must be 2");
      }
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      MethodHandle$2$F.invokeExact(
          values$MemorySegment$f);
      java.lang.foreign.MemorySegment.copy(
          values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("bytes"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void bytes(
      java.nio.ByteBuffer values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values.position();
      var values$size$f = values.remaining();
      if (values.isReadOnly()) {
        throw new IllegalArgumentException(
            "values must be writable unless annotated @In");
      }
      var values$direct$f = values.isDirect();
      var values$MemorySegment$f = values$direct$f
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BYTE.byteSize(), (long) values$size$f))
          : arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_BYTE, values$size$f);
      if (!values$direct$f) {
        for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
          values$MemorySegment$f.setAtIndex(java.lang.foreign.ValueLayout.JAVA_BYTE, values$index$f,
              values.get(values$position$f + values$index$f));
        }
      }
      MethodHandle$3$F.invokeExact(
          values$MemorySegment$f);
      if (!values$direct$f) {
        for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
          values.put(values$position$f + values$index$f,
              values$MemorySegment$f.getAtIndex(java.lang.foreign.ValueLayout.JAVA_BYTE, values$index$f));
        }
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$4$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("ints"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void ints(
      java.nio.IntBuffer values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values.position();
      var values$size$f = values.remaining();
      if (values$size$f != 2) {
        throw new IllegalArgumentException(
            "values remaining must be 2");
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
      var values$direct$f = values.isDirect();
      var values$MemorySegment$f = values$direct$f
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f))
          : arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      MethodHandle$4$F.invokeExact(
          values$MemorySegment$f);
      if (!values$direct$f) {
        for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
          values.put(values$position$f + values$index$f,
              values$MemorySegment$f.getAtIndex(java.lang.foreign.ValueLayout.JAVA_INT, values$index$f));
        }
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$5$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("flags"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void flags(
      boolean[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_BOOLEAN, values$size$f);
      for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
        values$MemorySegment$f.setAtIndex(
            java.lang.foreign.ValueLayout.JAVA_BOOLEAN, values$index$f,
            values[values$index$f]);
      }
      MethodHandle$5$F.invokeExact(
          values$MemorySegment$f);
      for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
        values[values$index$f] = values$MemorySegment$f.getAtIndex(
            java.lang.foreign.ValueLayout.JAVA_BOOLEAN, values$index$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$6$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("prefix"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public void prefix(
      int[] values,
      int count) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$available$f = values.length;
      var values$count$f = (long) count;
      if (values$count$f < 0L || values$count$f > values$available$f) {
        throw new IllegalArgumentException(
            "values count parameter 'count' must be between 0 and "
                + values$available$f + " (length): " + values$count$f);
      }
      var values$size$f = (int) values$count$f;
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      MethodHandle$6$F.invokeExact(
          values$MemorySegment$f,
          count);
      java.lang.foreign.MemorySegment.copy(
          values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("transform"),
      java.lang.foreign.FunctionDescriptor.of(
          java.lang.foreign.ValueLayout.JAVA_INT,
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_LONG));

  public int transform(
      pkg.CallPoint[] points,
      long count) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$available$f = points.length;
      var points$count$f = (long) count;
      if (points$count$f < 0L || points$count$f > points$available$f) {
        throw new IllegalArgumentException(
            "points count parameter 'count' must be between 0 and "
                + points$available$f + " (length): " + points$count$f);
      }
      var points$size$f = (int) points$count$f;
      var points$MemorySegment$f = arena$f.allocate(
          pkg.CallPointFM.MemoryLayout$F, points$size$f);
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        points$MemorySegment$f.asSlice(
            (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
            pkg.CallPointFM.MemoryLayout$F).copyFrom(
                pkg.CallPointFM.toMemorySegment$F(
                    arena$f, points[points$index$f]));
      }
      var result$f = (int) MethodHandle$7$F.invokeExact(
          points$MemorySegment$f,
          count);
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        points[points$index$f] = pkg.CallPointFM.fromMemorySegment$F(
            points$MemorySegment$f.asSlice(
                (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
                pkg.CallPointFM.MemoryLayout$F));
      }
      return result$f;
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$8$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("produce"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void produce(
      pkg.@org.alveolo.ffm.Out CallPoint[] points) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$size$f = points.length;
      var points$MemorySegment$f = arena$f.allocate(
          pkg.CallPointFM.MemoryLayout$F, points$size$f);
      MethodHandle$8$F.invokeExact(
          points$MemorySegment$f);
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        points[points$index$f] = pkg.CallPointFM.fromMemorySegment$F(
            points$MemorySegment$f.asSlice(
                (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
                pkg.CallPointFM.MemoryLayout$F));
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$9$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("consume"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS));

  public void consume(
      pkg.@org.alveolo.ffm.In @org.alveolo.ffm.Sequence(2L) CallPoint[] points) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$size$f = points.length;
      if (points$size$f != 2) {
        throw new IllegalArgumentException(
            "points length must be 2");
      }
      var points$MemorySegment$f = arena$f.allocate(
          pkg.CallPointFM.MemoryLayout$F, points$size$f);
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        points$MemorySegment$f.asSlice(
            (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
            pkg.CallPointFM.MemoryLayout$F).copyFrom(
                pkg.CallPointFM.toMemorySegment$F(
                    arena$f, points[points$index$f]));
      }
      MethodHandle$9$F.invokeExact(
          points$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$10$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("readPrefix"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.ValueLayout.ADDRESS,
          java.lang.foreign.ValueLayout.JAVA_INT));

  public void readPrefix(
      java.nio.IntBuffer values,
      int count) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values.position();
      var values$available$f = values.remaining();
      var values$count$f = (long) count;
      if (values$count$f < 0L || values$count$f > values$available$f) {
        throw new IllegalArgumentException(
            "values count parameter 'count' must be between 0 and "
                + values$available$f + " (remaining): " + values$count$f);
      }
      var values$size$f = (int) values$count$f;
      if (values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var values$direct$f = values.isDirect();
      var values$MemorySegment$f = values$direct$f
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f))
          : arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      if (!values$direct$f) {
        for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
          values$MemorySegment$f.setAtIndex(java.lang.foreign.ValueLayout.JAVA_INT, values$index$f,
              values.get(values$position$f + values$index$f));
        }
      }
      MethodHandle$10$F.invokeExact(
          values$MemorySegment$f,
          count);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$11$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("valueArray"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(3L, java.lang.foreign.ValueLayout.JAVA_INT))));

  public void valueArray(
      @org.alveolo.ffm.Value @org.alveolo.ffm.Sequence(3L) int[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      java.lang.foreign.MemorySegment.copy(
          values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      MethodHandle$11$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$12$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("valueBuffer"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(2L, java.lang.foreign.ValueLayout.JAVA_INT))));

  public void valueBuffer(
      java.nio.IntBuffer values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values.position();
      var values$size$f = values.remaining();
      if (values$size$f != 2) {
        throw new IllegalArgumentException(
            "values remaining must be 2");
      }
      if (values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var values$direct$f = values.isDirect();
      var values$MemorySegment$f = values$direct$f
          ? java.lang.foreign.MemorySegment.ofBuffer(values).asSlice(
              0L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f))
          : arena$f.allocate(java.lang.foreign.ValueLayout.JAVA_INT, values$size$f);
      if (!values$direct$f) {
        for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
          values$MemorySegment$f.setAtIndex(java.lang.foreign.ValueLayout.JAVA_INT, values$index$f,
              values.get(values$position$f + values$index$f));
        }
      }
      MethodHandle$12$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$13$F =
      Linker$F.downcallHandle(
      SymbolLookup$F.findOrThrow("valueRecords"),
      java.lang.foreign.FunctionDescriptor.ofVoid(
          java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(2L, pkg.CallPointFM.MemoryLayout$F))));

  public void valueRecords(
      pkg.@org.alveolo.ffm.Value @org.alveolo.ffm.Sequence(2L) CallPoint[] values) {
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 2) {
        throw new IllegalArgumentException(
            "values length must be 2");
      }
      var values$MemorySegment$f = arena$f.allocate(
          pkg.CallPointFM.MemoryLayout$F, values$size$f);
      for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
        values$MemorySegment$f.asSlice(
            (long) values$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
            pkg.CallPointFM.MemoryLayout$F).copyFrom(
                pkg.CallPointFM.toMemorySegment$F(
                    arena$f, values[values$index$f]));
      }
      MethodHandle$13$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}
