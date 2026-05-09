package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;

import org.junit.jupiter.api.Disabled;
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

  @Disabled
  @Test
  void wrapsStringInArena() {
    var source = forSourceString("test.LibC", """
        package test;
        import org.alveolo.ffm.ForeignInterface;
        @ForeignInterface
        public interface LibC {
          String toString(int value);
        }
        """);
    var compilation = compile(source);
    assertThat(compilation).succeeded();

    String generated = getGeneratedSource(compilation, "test.LibCFFM");
    assertContains(generated, "Arena.ofConfined()");
    assertContains(generated, "ff$arena");
  }

  @Test
  void loadsFramework() {
    var source = forSourceString("test.LibC", """
        package test;
        import org.alveolo.ffm.ForeignInterface;
        import org.alveolo.ffm.macos.Framework;
        @ForeignInterface
        @Framework("CoreFoundation")
        public interface LibC {}
        """);
    var compilation = compile(source);
    assertThat(compilation).succeeded();

    String generated = getGeneratedSource(compilation, "test.LibCFFM");
    assertContains(generated, "System.load(\"/System/Library/Frameworks"
        + "/CoreFoundation.framework/Versions/Current/CoreFoundation\")");
  }

  @Test
  void loadsMultipleFrameworks() {
    var source = forSourceString("test.LibC", """
        package test;
        import org.alveolo.ffm.ForeignInterface;
        import org.alveolo.ffm.macos.Frameworks;
        import org.alveolo.ffm.macos.Framework;
        @ForeignInterface
        @Frameworks({@Framework("A"), @Framework("B")})
        public interface LibC {}
        """);
    var compilation = compile(source);
    assertThat(compilation).succeeded();

    String generated = getGeneratedSource(compilation, "test.LibCFFM");
    assertContains(generated, "/Frameworks/A.framework");
    assertContains(generated, "/Frameworks/B.framework");
  }

  @Test
  void failsOnNonInterface() {
    var source = forSourceString("test.BadClass", """
        package pkg;
        import org.alveolo.ffm.ForeignInterface;
        @ForeignInterface
        public class BadClass {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@FFM is only allowed on interfaces");
  }
}
