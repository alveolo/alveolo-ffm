package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class ZeroSizedLayoutTest extends AbstractProcessorTest {
  @Test
  void generatesOnlySingleObjectHelpers() {
    var c = compile("memory/zero/ZeroSized.java");

    assertThat(c).succeeded();
    assertGenerated(c, "pkg.OpaqueFM", "memory/zero/OpaqueFM.java");
    assertGenerated(c, "pkg.EmptyUnionFM", "memory/zero/EmptyUnionFM.java");
    assertGenerated(c, "pkg.EmptyRecordFM", "memory/zero/EmptyRecordFM.java");
    assertGenerated(c, "pkg.ZeroCallsFFM", "memory/zero/ZeroCallsFFM.java");
  }

  @Test
  void allowsAddressUsesAndEmptyMarkerBase() {
    var source = forSourceString("test.Types", """
        package test;
        import org.alveolo.ffm.*;
        @Struct interface EmptyBase {}
        @Union interface EmptyUnion {}
        @Struct interface Derived extends EmptyBase { int value(); }
        @Struct record EmptyRecord() {}
        @Struct interface Pointers {
          EmptyBase items(@Sequence(2) long index);
        }
        @ForeignInterface interface Calls {
          EmptyBase echo(EmptyBase value);
          EmptyUnion echoUnion(EmptyUnion value);
          @Address EmptyRecord snapshot(@Address EmptyRecord value);
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void rejectsZeroSizedValueFieldsAndIndexedElements() {
    var source = forSourceString("test.Types", """
        package test;
        import org.alveolo.ffm.*;
        @Struct interface Empty {}
        @Union interface EmptyUnion {}
        @Struct record EmptyRecord() {}
        @Struct interface Scalar {
          int preserved();
          @Value Empty value();
        }
        @Struct interface Fixed {
          int preserved();
          @Value Empty value(@Sequence(2) long index);
        }
        @Struct interface UnionValue {
          int preserved();
          @Value EmptyUnion value();
        }
        @Struct record Nested(int preserved, EmptyRecord value) {}
        @Struct record Batch(
            int preserved, @Sequence(2) EmptyRecord[] values) {}
        class Use {
          int read(ScalarFM scalar, FixedFM fixed) {
            return scalar.preserved() + fixed.preserved();
          }
        }
        """);
    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("Empty value()");
    assertThat(c).hadErrorContaining(
        "Native array elements must have nonzero size")
        .inFile(source).onLineContaining("value(@Sequence");
    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("Nested(int preserved");
    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("EmptyUnion value()");
    assertThat(c).hadErrorContaining(
        "Native array elements must have nonzero size")
        .inFile(source).onLineContaining("EmptyRecord[] values");
    assertThat(c).hadErrorCount(5);
  }

  @Test
  void rejectsZeroSizedNativeValueUses() {
    var source = forSourceString("test.Calls", """
        package test;
        import java.lang.foreign.SegmentAllocator;
        import org.alveolo.ffm.*;
        @Struct interface Empty {}
        @Struct record EmptyRecord() {}
        @ForeignInterface interface Calls {
          @Value Empty returnInterface(SegmentAllocator allocator);
          void interfaceParameter(@Value Empty value);
          EmptyRecord returnRecord();
          void recordParameter(EmptyRecord value);
          void records(EmptyRecord[] values);
        }
        """);
    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("returnInterface");
    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("interfaceParameter");
    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("returnRecord");
    assertThat(c).hadErrorContaining(
        "Zero-sized native types can only be used by address")
        .inFile(source).onLineContaining("recordParameter");
    assertThat(c).hadErrorContaining(
        "Native array elements must have nonzero size")
        .inFile(source).onLineContaining("records(EmptyRecord[]");
    assertThat(c).hadErrorCount(5);
  }
}
