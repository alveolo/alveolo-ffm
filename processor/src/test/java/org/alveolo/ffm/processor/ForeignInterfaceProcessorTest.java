package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;

import org.junit.jupiter.api.Test;

class ForeignInterfaceProcessorTest extends AbstractProcessorTest {
  @Test
  void x() {
    Linker.nativeLinker().canonicalLayouts()
        .forEach((name, value) -> System.out
            .println("type: " + name + ",\tvalue: " + value.toString()
                + ",\tbyteSize: " + value.byteSize()
                + ",\tbyteAlignment: " + value.byteAlignment()
                + ",\tbyteOffset: " + value.byteOffset()));
  }

  @Test
  void generatesEmptyFFM() {
    var c = compile("interface/Empty.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.EmptyFFM", "interface/EmptyFFM.java");
  }

  @Test
  void generatesBasicFFM() {
    var c = compile("interface/LibC.java", "value/div_t.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.LibCFFM", "interface/LibCFFM.java");
    assertGenerated(c, "pkg.div_tFM", "value/div_tFM.java");
  }

  @Test
  void generatesMacFrameworkFFM() {
    var c = compile("interface/CoreFramework.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.CoreFrameworkFFM",
        "interface/CoreFrameworkFFM.java");
  }

  @Test
  void generatesMultiMacFrameworkFFM() {
    var c = compile("interface/MultiFramework.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.MultiFrameworkFFM",
        "interface/MultiFrameworkFFM.java");
  }

  @Test
  void failsOnNonInterface() {
    var source = forSourceString("test.BadClass", """
        package pkg;
        @org.alveolo.ffm.ForeignInterface
        public class BadClass {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@ForeignInterface is only allowed on interfaces");
  }
}
