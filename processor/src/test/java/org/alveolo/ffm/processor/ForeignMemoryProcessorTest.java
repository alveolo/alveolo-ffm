package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesStructClass() {
    var c = compile("memory/struct/timeval.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.timevalFM", "memory/struct/timevalFM.java");
  }

  @Test
  void generatesMultipleStructs() {
    var c = compile(
        "memory/struct/multiple/StructA.java",
        "memory/struct/multiple/StructB.java");
    assertThat(c).succeeded();
    assertGenerated(c,
        "pkg.StructAFM", "memory/struct/multiple/StructAFM.java");
    assertGenerated(c,
        "pkg.StructBFM", "memory/struct/multiple/StructBFM.java");
  }

  @Test
  void generatesUnionClass() {
    var c = compile("memory/union/Union.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.UnionFM", "memory/union/UnionFM.java");
  }

  @Test
  void generatesNestedStruct() {
    var c = compile("memory/nested/Inner.java", "memory/nested/Outer.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.InnerFM", "memory/nested/InnerFM.java");
    assertGenerated(c, "pkg.OuterFM", "memory/nested/OuterFM.java");
  }

  @Test
  void generatesPassModeFields() {
    var c = compile("memory/passmode/FieldModes.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.FieldModesFM",
        "memory/passmode/FieldModesFM.java");
    assertGenerated(c, "pkg.FieldModeAccessorsFM",
        "memory/passmode/FieldModeAccessorsFM.java");
  }

  @Test
  void failsStructOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.Struct
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@Struct can only be applied to an interface, not ENUM");
  }

  @Test
  void failsUnionOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.Union
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not ENUM");
  }

  @Test
  void failsStructOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.Struct
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct can only be applied to an interface, not CLASS");
  }

  @Test
  void failsUnionOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.Union
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not CLASS");
  }

  @Test
  void failsUnionOnRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Union
        public record BadRecord(int a) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not RECORD");
  }

  @Test
  void failsStructOnRecordWithUnsupportedType() {
    var source = forSourceString("test.BadRecord", """
        package test;
        import org.alveolo.ffm.Struct;
        import java.util.List;
        @Struct
        public record BadRecord(List<String> data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Type is not supported: java.util.List");
  }
}
