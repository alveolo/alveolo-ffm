package org.alveolo.ffm.processor;

import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesStructClass() {
    var c = compile("memory/struct/package-info.java");
    assertGenerated(c, "pkg.Struct", "memory/struct/Struct.java");
  }

  @Test
  void generatesMultipleStructs() {
    var c = compile("memory/struct/multiple/package-info.java");
    assertGenerated(c, "pkg.Struct", "memory/struct/Struct.java");
    assertGenerated(c, "pkg.Struct2", "memory/struct/multiple/Struct2.java");
  }

  @Test
  void generatesUnionClass() {
    var compilation = compile("memory/union/package-info.java");
    assertGenerated(compilation, "pkg.Union", "memory/union/Union.java");
  }

  @Test
  void generatesMultipleUnions() {
    var c = compile("memory/union/multiple/package-info.java");
    assertGenerated(c, "pkg.Union", "memory/union/Union.java");
    assertGenerated(c, "pkg.Union2", "memory/union/multiple/Union2.java");
  }

  @Test
  void failsOnNonPackageElement() {
    // @ForeignStruct on an enum is rejected by the compiler (wrong target),
    // so no class should be generated
    var source = forSourceString("test.BadEnum", """
        package test;
        import org.alveolo.ffm.*;
        @ForeignStruct(name="Bad", elements={
          @Element(name="x", type=int.class)
        })
        public enum BadEnum { A, B }
        """);
    var compilation = compile(source);
    assertFalse(compilation.errors().isEmpty()); // TODO
    // assertFalse(compilation.generatedSourceFile("test.Bad").isPresent(),
    // "Expected no class to be generated for @ForeignStruct on an enum");
  }
}
