package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FlexibleArrayTest extends AbstractProcessorTest {
  @Test
  void generatesCountedRowsAndExternallySizedPackets() {
    var c = compile("memory/flexible/FlexibleFields.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.NamesFM", "memory/flexible/NamesFM.java");
    assertGenerated(c, "pkg.TransferFM", "memory/flexible/TransferFM.java");
    assertGenerated(c, "pkg.TransfersFFM", "memory/flexible/TransfersFFM.java");
  }

  @Test
  void resolvesTheMappedFieldAcrossADiamondAndAnOverride() {
    var source = forSourceString("test.Headers", """
        package test;
        import org.alveolo.ffm.*;
        import org.alveolo.ffm.processor.fixture.BinaryFlexibleRows;
        interface Left extends BinaryFlexibleRows {}
        interface Right extends BinaryFlexibleRows {}
        @Struct interface Sized extends Left, Right {
          @Override byte data(@Flexible long i);
        }
        interface Pattern { byte data(@Flexible long i); }
        @Struct interface Fixed extends Pattern {
          @Override byte data(@Sequence(2) long i);
        }
        @Fields("header") interface Header { int header(); }
        @Struct interface Independent extends Header {
          byte data(@Flexible long i);
        }
        @ForeignInterface interface Api {
          Sized get();
          void put(@Value Fixed fixed, Independent sizedByCaller);
        }
        """);
    assertThat(compile(source)).succeeded();
  }

  @Test
  void preservesOtherFieldsAfterRejectingAFlexibleMember() {
    var source = forSourceString("test.Mixed", """
        package test;
        import org.alveolo.ffm.*;
        @Struct interface Mixed {
          int header();
          byte tail(@Flexible long index);
          int preserved();
        }
        class Use {
          int read(MixedFM value) {
            return value.header() + value.preserved();
          }
        }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining("last field").inFile(source)
        .onLineContaining("byte tail");
    assertThat(c).hadErrorCount(1);
  }

  @ParameterizedTest
  @CsvSource(delimiter = '|', textBlock = """
      @Struct interface Bad { int a(@Flexible int i); } | preceding header field
      @Struct interface Bad { int count(); byte a(@Flexible long i); int after(); } | last field
      @Struct interface Bad { int count(); byte a(@Flexible long i); byte b(@Flexible long i); } | last field
      @Union interface Bad { int count(); byte a(@Flexible int i); } | last field of a @Struct
      @Struct interface Bad { int count(); byte a(@Sequence(2) int i, @Flexible int j); } | outermost dimension
      @Struct interface Bad { int count(); byte a(@Flexible int i, @Flexible int j); } | outermost dimension
      @Struct interface Bad { int count(); byte a(@Flexible @Sequence(2) int i); } | cannot share
      @Struct interface Bad { int count(); byte a(@Flexible double i); } | parameters must be int or long
      @Struct interface Bad { int count(); byte a(@Flexible("typo") int i); } | integral scalar header field
      @Struct interface Bad { float count(); byte a(@Flexible("count") int i); } | integral scalar header field
      @Struct interface Bad { int count(@Sequence(1) int i); byte a(@Flexible("count") int i); } | integral scalar header field
      @Struct interface Bad { int count(); byte a(@Flexible("a") int i); } | integral scalar header field
      @Struct record Bad(@Flexible int n) {} | indexed struct field parameter
      @Struct record Bad(@Flexible @Sequence(2) byte[] data) {} | indexed struct field parameter
      @ForeignInterface interface Bad { int call(@Flexible int n); } | indexed struct field parameter
      interface Parent { int call(@Flexible long n); } @ForeignInterface interface Bad extends Parent {} | indexed struct field parameter
      interface Parent { @Slot(0) int call(@Flexible long n); } @DispatchTable interface Bad extends Parent {} | indexed struct field parameter
      interface Parent { @Virtual(0) int call(@Flexible long n); } @Struct(vtable = true) interface Bad extends Parent {} | indexed struct field parameter
      @Fields({"n", "data"}) interface External { int n(); byte data(@Flexible long i); } @Struct interface Child extends External { @Override byte data(@Flexible("n") long i); } @ForeignInterface interface Bad { Child get(); } | explicit flexibleLength
      @Struct interface Bad extends Tail {} | inheritance requires fixed-size structs
      @Struct interface Prefix { int n(); } @Struct interface Bad extends Prefix { byte tail(@Flexible long i); } | inheritance requires fixed-size structs
      @Struct interface Prefix { int n(); } @Struct interface Bad extends Prefix { byte tail(@Flexible("n") long i); } | inheritance requires fixed-size structs
      @Struct interface Bad { @Value Tail data(); } | cannot be used by value
      @Struct interface Bad { @Value TailFM data(@Sequence(2) long i); } | cannot be used by value
      @Struct interface Bad { int n(); @Value Tail data(@Flexible long i); } | cannot be used by value
      @Struct interface Bad { int n(); @Value Empty data(@Flexible long i); } | nonzero size
      @Struct interface Bad { Tail pointer(); } | explicit flexibleLength
      @ForeignInterface interface Bad { TailFM get(); } | explicit flexibleLength
      @ForeignInterface interface Bad { void send(@Value Tail value); } | cannot be used by value
      @ForeignInterface interface Bad { @Value Tail get(java.lang.foreign.SegmentAllocator allocator); } | cannot be used by value
      """)
  void rejectsInvalidFlexibleDeclarations(String bad, String diagnostic) {
    var source = forSourceString("test.Bad", """
        package test;
        import org.alveolo.ffm.*;
        @Struct interface Tail { int header(); int data(@Flexible long i); }
        @Struct interface Empty {}
        %s
        """.formatted(bad));
    var c = compile(source);
    assertThat(c).hadErrorContaining(diagnostic).inFile(source);
    assertThat(c).hadErrorCount(1);
  }
}
