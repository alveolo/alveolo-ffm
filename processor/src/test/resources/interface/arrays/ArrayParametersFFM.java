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
      SymbolLookup$F.find("scale")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void scale(
      int[] values) {
    if (MethodHandle$0$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: scale");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      }
      MethodHandle$0$F.invokeExact(
          values$MemorySegment$f);
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$1$F =
      SymbolLookup$F.find("sum")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public int sum(
      @org.alveolo.ffm.In @org.alveolo.ffm.Sequence(3L) int[] values) {
    if (MethodHandle$1$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: sum");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      if (values != null && values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      }
      return (int) MethodHandle$1$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$2$F =
      SymbolLookup$F.find("fill")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void fill(
      @org.alveolo.ffm.Out @org.alveolo.ffm.Sequence(2L) int[] values) {
    if (MethodHandle$2$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: fill");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      if (values != null && values$size$f != 2) {
        throw new IllegalArgumentException(
            "values length must be 2");
      }
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      MethodHandle$2$F.invokeExact(
          values$MemorySegment$f);
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$3$F =
      SymbolLookup$F.find("bytes")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void bytes(
      java.nio.ByteBuffer values) {
    if (MethodHandle$3$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: bytes");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values == null ? 0 : values.position();
      var values$size$f = values == null ? 0 : values.remaining();
      if (values != null && values.isReadOnly()) {
        throw new IllegalArgumentException(
            "values must be writable unless annotated @In");
      }
      var values$direct$f = values != null && values$size$f != 0 && values.isDirect();
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : values$direct$f
              ? java.lang.foreign.MemorySegment.ofBuffer(values)
              : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BYTE.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_BYTE.byteAlignment());
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
      SymbolLookup$F.find("ints")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void ints(
      java.nio.IntBuffer values) {
    if (MethodHandle$4$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: ints");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values == null ? 0 : values.position();
      var values$size$f = values == null ? 0 : values.remaining();
      if (values != null && values$size$f != 2) {
        throw new IllegalArgumentException(
            "values remaining must be 2");
      }
      if (values != null && values.isReadOnly()) {
        throw new IllegalArgumentException(
            "values must be writable unless annotated @In");
      }
      if (values != null && values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var values$direct$f = values != null && values$size$f != 0 && values.isDirect();
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : values$direct$f
              ? java.lang.foreign.MemorySegment.ofBuffer(values)
              : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
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
      SymbolLookup$F.find("flags")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void flags(
      boolean[] values) {
    if (MethodHandle$5$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: flags");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_BOOLEAN.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_BOOLEAN.byteAlignment());
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
      SymbolLookup$F.find("process")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public void process(
      int[] values,
      int count) {
    if (MethodHandle$6$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: process");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values == null ? 0 : values.length;
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      }
      MethodHandle$6$F.invokeExact(
          values$MemorySegment$f,
          count);
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values, 0, values$size$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$7$F =
      SymbolLookup$F.find("transform")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.of(
                  java.lang.foreign.ValueLayout.JAVA_INT,
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_LONG)))
          .orElse(null);

  public int transform(
      pkg.CallPoint[] points,
      long count) {
    if (MethodHandle$7$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: transform");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$size$f = points == null ? 0 : points.length;
      var points$MemorySegment$f = points == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(pkg.CallPointFM.MemoryLayout$F.byteSize(), (long) points$size$f)),
              pkg.CallPointFM.MemoryLayout$F.byteAlignment());
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        pkg.CallPointFM.toMemorySegment$F(
            points[points$index$f],
            points$MemorySegment$f.asSlice(
                (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
                pkg.CallPointFM.MemoryLayout$F));
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
      SymbolLookup$F.find("produce")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void produce(
      pkg.@org.alveolo.ffm.Out CallPoint[] points) {
    if (MethodHandle$8$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: produce");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$size$f = points == null ? 0 : points.length;
      var points$MemorySegment$f = points == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(pkg.CallPointFM.MemoryLayout$F.byteSize(), (long) points$size$f)),
              pkg.CallPointFM.MemoryLayout$F.byteAlignment());
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
      SymbolLookup$F.find("consume")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void consume(
      pkg.@org.alveolo.ffm.In @org.alveolo.ffm.Sequence(2L) CallPoint[] points) {
    if (MethodHandle$9$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: consume");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var points$size$f = points == null ? 0 : points.length;
      if (points != null && points$size$f != 2) {
        throw new IllegalArgumentException(
            "points length must be 2");
      }
      var points$MemorySegment$f = points == null
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(pkg.CallPointFM.MemoryLayout$F.byteSize(), (long) points$size$f)),
              pkg.CallPointFM.MemoryLayout$F.byteAlignment());
      for (var points$index$f = 0; points$index$f < points$size$f; points$index$f++) {
        pkg.CallPointFM.toMemorySegment$F(
            points[points$index$f],
            points$MemorySegment$f.asSlice(
                (long) points$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
                pkg.CallPointFM.MemoryLayout$F));
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
      SymbolLookup$F.find("read")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.JAVA_INT)))
          .orElse(null);

  public void read(
      java.nio.IntBuffer values,
      int count) {
    if (MethodHandle$10$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: read");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$position$f = values == null ? 0 : values.position();
      var values$size$f = values == null ? 0 : values.remaining();
      if (values != null && values.isDirect()
          && !values.order().equals(java.nio.ByteOrder.nativeOrder())) {
        throw new IllegalArgumentException(
            "direct values must use native byte order");
      }
      var values$direct$f = values != null && values$size$f != 0 && values.isDirect();
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : values$direct$f
              ? java.lang.foreign.MemorySegment.ofBuffer(values)
              : arena$f.allocate(
              Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f)),
              java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
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
      SymbolLookup$F.find("valueArray")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(3L, java.lang.foreign.ValueLayout.JAVA_INT)))))
          .orElse(null);

  public void valueArray(
      @org.alveolo.ffm.Value @org.alveolo.ffm.Sequence(3L) int[] values) {
    if (MethodHandle$11$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: valueArray");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 3) {
        throw new IllegalArgumentException(
            "values length must be 3");
      }
      var values$MemorySegment$f = arena$f.allocate(
          Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f),
          java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_INT, 0, values$size$f);
      }
      MethodHandle$11$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$12$F =
      SymbolLookup$F.find("valueBuffer")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(2L, java.lang.foreign.ValueLayout.JAVA_INT)))))
          .orElse(null);

  public void valueBuffer(
      java.nio.IntBuffer values) {
    if (MethodHandle$12$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: valueBuffer");
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
          ? java.lang.foreign.MemorySegment.ofBuffer(values)
          : arena$f.allocate(
          Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_INT.byteSize(), (long) values$size$f),
          java.lang.foreign.ValueLayout.JAVA_INT.byteAlignment());
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
      SymbolLookup$F.find("valueRecords")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.MemoryLayout.structLayout(java.lang.foreign.MemoryLayout.sequenceLayout(2L, pkg.CallPointFM.MemoryLayout$F)))))
          .orElse(null);

  public void valueRecords(
      pkg.@org.alveolo.ffm.Value @org.alveolo.ffm.Sequence(2L) CallPoint[] values) {
    if (MethodHandle$13$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: valueRecords");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var values$size$f = values.length;
      if (values$size$f != 2) {
        throw new IllegalArgumentException(
            "values length must be 2");
      }
      var values$MemorySegment$f = arena$f.allocate(
          Math.multiplyExact(pkg.CallPointFM.MemoryLayout$F.byteSize(), (long) values$size$f),
          pkg.CallPointFM.MemoryLayout$F.byteAlignment());
      for (var values$index$f = 0; values$index$f < values$size$f; values$index$f++) {
        pkg.CallPointFM.toMemorySegment$F(
            values[values$index$f],
            values$MemorySegment$f.asSlice(
                (long) values$index$f * pkg.CallPointFM.MemoryLayout$F.byteSize(),
                pkg.CallPointFM.MemoryLayout$F));
      }
      MethodHandle$13$F.invokeExact(
          values$MemorySegment$f);
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }

  private static final java.lang.invoke.MethodHandle MethodHandle$14$F =
      SymbolLookup$F.find("textAndValues")
          .map(address$f -> Linker$F.downcallHandle(
              address$f,
              java.lang.foreign.FunctionDescriptor.ofVoid(
                  java.lang.foreign.ValueLayout.ADDRESS,
                  java.lang.foreign.ValueLayout.ADDRESS)))
          .orElse(null);

  public void textAndValues(
      java.lang.String text,
      long[] values) {
    if (MethodHandle$14$F == null)
      throw new UnsatisfiedLinkError("Native symbol not found: textAndValues");
    try (var arena$f = java.lang.foreign.Arena.ofConfined()) {
      var text$bytes$f = text == null ? null : text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
      var values$size$f = values == null ? 0 : values.length;
      var text$allocationOffset$f = 0L;
      var allocationOffset$f = (text == null ? 0L : Math.addExact((long) text$bytes$f.length, 1L));
      allocationOffset$f = Math.addExact(
          allocationOffset$f,
          Math.floorMod(-allocationOffset$f, java.lang.foreign.ValueLayout.JAVA_LONG.byteAlignment()));
      var values$allocationOffset$f = allocationOffset$f;
      allocationOffset$f = Math.addExact(
          allocationOffset$f, (values == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_LONG.byteSize(), (long) values$size$f))));
      var allocation$MemorySegment$f = allocationOffset$f == 0L
          ? java.lang.foreign.MemorySegment.NULL
          : arena$f.allocate(allocationOffset$f, java.lang.foreign.ValueLayout.JAVA_LONG.byteAlignment());
      var text$MemorySegment$f = text == null
          ? java.lang.foreign.MemorySegment.NULL : allocation$MemorySegment$f.asSlice(
          text$allocationOffset$f, (text == null ? 0L : Math.addExact((long) text$bytes$f.length, 1L)));
      if (text != null) {
        java.lang.foreign.MemorySegment.copy(
            text$bytes$f, 0, text$MemorySegment$f,
            java.lang.foreign.ValueLayout.JAVA_BYTE, 0, text$bytes$f.length);
        text$MemorySegment$f.set(
            java.lang.foreign.ValueLayout.JAVA_BYTE, text$bytes$f.length,
            (byte) 0);
      }
      var values$MemorySegment$f = values == null
          ? java.lang.foreign.MemorySegment.NULL
          : allocation$MemorySegment$f.asSlice(
              values$allocationOffset$f, (values == null ? 0L : Math.max(1L, Math.multiplyExact(java.lang.foreign.ValueLayout.JAVA_LONG.byteSize(), (long) values$size$f))));
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values, 0, values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_LONG, 0, values$size$f);
      }
      MethodHandle$14$F.invokeExact(
          text$MemorySegment$f,
          values$MemorySegment$f);
      if (values$size$f != 0) {
        java.lang.foreign.MemorySegment.copy(
            values$MemorySegment$f, java.lang.foreign.ValueLayout.JAVA_LONG, 0, values, 0, values$size$f);
      }
    } catch (RuntimeException|Error exception$f) {
      throw exception$f;
    } catch (Throwable throwable$f) {
      throw new AssertionError(throwable$f);
    }
  }
}
