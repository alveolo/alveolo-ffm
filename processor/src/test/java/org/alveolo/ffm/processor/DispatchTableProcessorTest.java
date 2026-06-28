package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class DispatchTableProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesDispatchTableFD() {
    var c = compile("dispatch/XyzVtbl.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.XyzVtblFD", "dispatch/XyzVtblFD.java");
  }

  @Test
  void failsOnNonInterface() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public class BadClass {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@DispatchTable is only allowed on interfaces");
  }

  @Test
  void failsOnMissingSlot() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          void missing();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Slot is required on @DispatchTable methods");
  }

  @Test
  void failsOnDuplicateSlot() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(index = 1) void a();
          @org.alveolo.ffm.Slot(index = 1) void b();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Duplicate @Slot index: 1");
  }

  @Test
  void failsOnNegativeSlot() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(index = -1) void bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@Slot index must be non-negative");
  }

  @Test
  void failsOnUnsupportedMethodType() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(index = 0) Object bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
  }
}
