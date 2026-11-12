package org.alveolo.ffm.processor;

import static org.alveolo.ffm.processor.ForeignMemoryAccessorGenerator.scalarGetterBody;
import static org.alveolo.ffm.processor.ForeignMemoryAccessorGenerator.scalarSetterBody;

import java.io.IOException;
import java.io.Writer;

import javax.lang.model.type.TypeKind;

/// Generates single-object allocation and wrapping for a struct with a
/// flexible array member. Explicit element counts exclude trailing ABI padding;
/// byte-sized views bound the complete object to the supplied extent.
record FlexibleArrayGenerator(IndexedField array, VariableGenerator count) {
  void writeStorage(Writer out, String className, boolean vtable) throws IOException {
    var name = array.element().name();
    var max = upperBound();
    if (max != null) {
      out.write("""

            private static long flexibleEndOffset$F(
                long baseOffset, <lengthType> flexibleLength) {
              if (flexibleLength > <max>) {
                throw new IllegalArgumentException("Invalid flexibleLength: " + flexibleLength);
              }
              return <name>$RowLayout$F.scale(baseOffset, flexibleLength);
            }
          """
          .replace("<name>", name)
          .replace("<lengthType>", lengthType())
          .replace("<max>", max));
    }

    var byteSize = "Math.max(MemoryLayout$F.byteSize(), " + scaleMethod() + "("
        + name + "$Offset$F, flexibleLength))";
    if (!vtable) {
      out.write("""

            public static java.lang.foreign.MemorySegment allocateFlexible$F(
                java.lang.foreign.SegmentAllocator allocator,
                <lengthType> flexibleLength) {
          """.replace("<lengthType>", lengthType()));
      var allocation = """
          allocator.allocate(
              <byteSize>,
              MemoryLayout$F.byteAlignment());
          """.replace("<byteSize>", byteSize);
      if (count == null) {
        out.write(("return " + allocation).indent(4));
      } else {
        out.write(("var memorySegment = " + allocation).indent(4));
        out.write(scalarSetterBody(
            count, "memorySegment", "flexibleLength", false).indent(4));
        out.write("    return memorySegment;\n");
      }
      out.write("  }\n");
    }

    if (count != null) {
      out.write("""

            public static <class> reinterpret$F(
                java.lang.foreign.MemorySegment memorySegment) {
              if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;
              <lengthType> flexibleLength = readFlexibleLength$F(
                  memorySegment.reinterpret(MemoryLayout$F.byteSize()));
              return reinterpretFlexible$F(memorySegment, flexibleLength);
            }
          """.replace("<class>", className)
          .replace("<lengthType>", lengthType()));
    }

    if (count == null) {
      out.write("""

            public static <class> reinterpret$F(
                java.lang.foreign.MemorySegment memorySegment, long byteSize) {
              if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;
              var bounded = memorySegment.reinterpret(byteSize);
              return new <class>(bounded, bounded.asSlice(<name>$Offset$F));
            }
          """.replace("<class>", className)
          .replace("<name>", name));
    }

    out.write("""

          public static <class> reinterpretFlexible$F(
              java.lang.foreign.MemorySegment memorySegment,
              <lengthType> flexibleLength) {
        """.replace("<class>", className)
        .replace("<lengthType>", lengthType()));
    out.write("    long byteSize = " + byteSize + ";\n");
    out.write("    if (memorySegment.equals(java.lang.foreign.MemorySegment.NULL)) return null;\n");
    out.write("""
            return new <class>(memorySegment.reinterpret(byteSize), flexibleLength);
          }
        """.replace("<class>", className));
  }

  String scaleMethod() {
    return upperBound() == null ? array.element().name() + "$RowLayout$F.scale" : "flexibleEndOffset$F";
  }

  String lengthType() {
    return count == null ? "long" : count.typeName();
  }

  private String upperBound() {
    var indexMax = array.dimensions().getFirst().typeName().equals("int")
        ? "Integer.MAX_VALUE" : null;
    if (count == null) return indexMax;
    if (indexMax != null && count.typeMirror.getKind() == TypeKind.LONG)
      return indexMax;
    if (!count.hasCanonicalScalar()) return null;
    return switch (count.typeMirror.getKind()) {
      case INT -> "(" + count.valueLayout()
          + ".byteSize() == 2 ? Character.MAX_VALUE : Integer.MAX_VALUE)";
      case LONG -> {
        var max32 = count.canonicalScalar == TypeGenerator.CanonicalScalar.SLONG
            ? "Integer.MAX_VALUE" : "0xffff_ffffL";
        yield "(" + count.valueLayout() + ".byteSize() == 4 ? " + max32 + " : Long.MAX_VALUE)";
      }
      default -> throw new AssertionError(count.typeMirror);
    };
  }

  void writeCountGetter(Writer out) throws IOException {
    out.write("""

          private static <type> readFlexibleLength$F(java.lang.foreign.MemorySegment memorySegment) {
        <read>
          }

          public <type> <name>() {
        <getter>
          }
        """.replace("<name>", count.name()).replace("<type>", count.typeName())
        .replace("<read>", scalarGetterBody(count, "memorySegment").indent(4).stripTrailing())
        .replace("<getter>", scalarGetterBody(count, "MemorySegment$F").indent(4).stripTrailing()));
  }
}
