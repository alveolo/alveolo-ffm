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
  void generatesDispatchTableNameOverrideInSourcePackage() {
    var c = compile("dispatch/RenamedVtbl.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.RenamedVtblFD", "dispatch/RenamedVtblFD.java");
  }

  @Test
  void failsQualifiedDispatchTableNameOverride() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable(name = "other.BadVtblFD")
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(0) void bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@DispatchTable name must be a simple Java class name");
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
  void failsNestedDispatchTable() {
    var source = forSourceString("test.Outer", """
        package test;
        class Outer {
          @org.alveolo.ffm.DispatchTable
          interface Vtbl {
            @org.alveolo.ffm.Slot(0)
            void call();
          }
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Nested @DispatchTable types are not yet supported");
    assertThat(c).hadErrorCount(1);
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
  void failsOnSlotWithoutValue() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot void bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@Slot value must be non-negative");
  }

  @Test
  void failsOnDuplicateSlot() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(1) void a();
          @org.alveolo.ffm.Slot(1) void b();
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
          @org.alveolo.ffm.Slot(-1) void bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@Slot value must be non-negative");
  }

  @Test
  void failsOnUnsupportedMethodType() {
    var source = forSourceString("test.BadVtbl", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface BadVtbl {
          @org.alveolo.ffm.Slot(0) Object bad();
        }
        """);
    var generatedUse = forSourceString("test.BadVtblUse", """
        package test;

        class BadVtblUse {
          Class<?> generatedClass = BadVtblFD.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenSequenceIsUsedOnScalarParameter() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.DispatchTable
        public interface Bad {
          @org.alveolo.ffm.Slot(0)
          void call(@org.alveolo.ffm.Sequence(2) int value);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Sequence is only supported on array and Buffer types");
    assertThat(c).hadErrorCount(1);
  }
}
