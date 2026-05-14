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
  void failsForeignStructOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.ForeignStruct
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@ForeignStruct can only be applied to an interface, not ENUM");
  }

  @Test
  void failsForeignUnionOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.ForeignUnion
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@ForeignUnion can only be applied to an interface, not ENUM");
  }

  @Test
  void failsForeignStructOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.ForeignStruct
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@ForeignStruct can only be applied to an interface, not CLASS");
  }

  @Test
  void failsForeignUnionOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.ForeignUnion
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@ForeignUnion can only be applied to an interface, not CLASS");
  }

  @Test
  void failsForeignUnionOnRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.ForeignUnion
        public record BadRecord(int a) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@ForeignUnion can only be applied to an interface, not RECORD");
  }

  @Test
  void failsForeignStructOnRecordWithUnsupportedType() {
    var source = forSourceString("test.BadRecord", """
        package test;
        import org.alveolo.ffm.ForeignStruct;
        import java.util.List;
        @ForeignStruct
        public record BadRecord(List<String> data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Type is not supported: java.util.List");
  }
}
