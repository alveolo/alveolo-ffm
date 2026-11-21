package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class GeneratedClassNamesTest extends AbstractProcessorTest {
  @Test
  void rejectsMultipleGeneratingAnnotations() {
    var source = forSourceString("test.Mixed", """
        package test;

        @org.alveolo.ffm.Callback
        @org.alveolo.ffm.ForeignInterface
        interface Mixed {
          void invoke();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Only one generating annotation is allowed: "
            + "@Callback, @ForeignInterface");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void reportsGeneratedNameCollision() {
    var structs = forSourceString("test.Structs", """
        package test;

        @org.alveolo.ffm.Struct
        interface Entry {}

        """);
    var callback = forSourceString("test.Callback", """
        package test;

        @org.alveolo.ffm.Callback
        interface EntryFSpec {
          void invoke();
        }
        """);

    var c = compile(structs, callback);

    assertThat(c).hadErrorContaining(
        "Attempt to recreate a file for type test.EntryF");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void reportsVtableNameConflictsOnTheStruct() {
    var source = forSourceString("test.Device", """
        package test;

        @org.alveolo.ffm.Struct(vtable = true)
        interface Device {
          @org.alveolo.ffm.Virtual(0)
          int call();
        }

        final class DeviceV {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Generated class name conflicts with existing type test.DeviceV: "
            + "test.DeviceV")
        .inFile(source);
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void invalidNameDoesNotClaimAnotherPackageOutput() {
    var invalid = forSourceString("a.Bad", """
        package a;

        @org.alveolo.ffm.Struct(name = "b.GoodF")
        interface Bad {}
        """);
    var valid = forSourceString("a.b.Good", """
        package a.b;

        @org.alveolo.ffm.Struct
        interface Good {
          int value();
        }

        class UseGood {
          GoodF value;
        }
        """);

    var c = compile(invalid, valid);

    assertThat(c).hadErrorContaining(
        "@Struct name must be a simple Java class name, not: b.GoodF")
        .inFile(invalid);
    assertThat(c).hadErrorCount(1);
  }
}
