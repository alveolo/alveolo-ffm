package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class CallbackProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesCallbackHelpers() {
    var c = compile("callback/Callbacks.java");

    assertThat(c).succeeded();
    assertGenerated(c, "pkg.BinaryF", "callback/BinaryF.java");
    assertGenerated(c, "pkg.Event", "callback/Event.java");
    assertGenerated(c, "pkg.Tick", "callback/Tick.java");
  }

  @Test
  void supportsInheritedMethodAndNameOverride() {
    var c = compile("callback/Inherited.java");

    assertThat(c).succeeded();
    assertGenerated(c, "test.Renamed", "callback/Renamed.java");
  }

  @Test
  void rejectsNonInterfacesAndNonFunctionalInterfaces() {
    var nonInterface = forSourceString("test.NonInterface", """
        package test;
        @org.alveolo.ffm.Callback
        record NonInterface(int value) {}
        """);
    var empty = forSourceString("test.Empty", """
        package test;
        @org.alveolo.ffm.Callback
        interface Empty {}
        """);
    var multiple = forSourceString("test.Multiple", """
        package test;
        @org.alveolo.ffm.Callback
        interface Multiple {
          void first();
          void second();
        }
        """);

    var c = compile(nonInterface, empty, multiple);

    assertThat(c).hadErrorContaining(
        "@Callback can only be applied to an interface, not RECORD")
        .inFile(nonInterface);
    assertThat(c).hadErrorContaining(
        "@Callback interface must have exactly one abstract method")
        .inFile(empty);
    assertThat(c).hadErrorContaining(
        "@Callback interface must have exactly one abstract method")
        .inFile(multiple);
    assertThat(c).hadErrorCount(3);
  }

  @Test
  void rejectsUnsupportedCarriers() {
    var reference = forSourceString("test.Reference", """
        package test;
        @org.alveolo.ffm.Callback
        interface Reference {
          String invoke(String value);
        }
        """);
    var array = forSourceString("test.Array", """
        package test;
        @org.alveolo.ffm.Callback
        interface Array {
          void invoke(int[] value);
        }
        """);
    var nativeScalar = forSourceString("test.NativeScalar", """
        package test;
        @org.alveolo.ffm.Callback
        interface NativeScalar {
          void invoke(@org.alveolo.ffm.SizeT long value);
        }
        """);

    var c = compile(reference, array, nativeScalar);

    assertThat(c).hadErrorContaining(
        "@Callback methods only support primitive types and MemorySegment");
    assertThat(c).hadErrorContaining(
        "@Callback methods do not yet support native carrier annotations")
        .inFile(nativeScalar);
    assertThat(c).hadErrorCount(4);
  }
}
