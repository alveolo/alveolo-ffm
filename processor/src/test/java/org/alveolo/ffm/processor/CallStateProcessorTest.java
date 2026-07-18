package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.alveolo.ffm.ForeignUtils;
import org.alveolo.ffm.Library;
import org.junit.jupiter.api.Test;

class CallStateProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesCallStateWrapper() {
    var c = compile("memory/callstate/NativeErrorSpec.java");

    assertThat(c).succeeded();
    assertGenerated(c, "pkg.NativeError",
        "memory/callstate/NativeError.java");
  }

  @Test
  void rejectsNonInterfaceCallState() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.CallState("errno")
        record Bad(int error) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@CallState can only be applied to an interface, not RECORD");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void rejectsBlankStateNames() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.CallState(
            value = " ",
            overrides = @org.alveolo.ffm.CallState.Override(value = ""))
        interface Bad {
          int error();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("@CallState value must not be blank");
    assertThat(c).hadErrorContaining(
        "@CallState.Override value must not be blank");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void rejectsMissingAndInvalidAccessors() {
    var missing = forSourceString("test.Missing", """
        package test;
        @org.alveolo.ffm.CallState("errno")
        interface Missing {}
        """);
    var invalid = forSourceString("test.Invalid", """
        package test;
        @org.alveolo.ffm.CallState("errno")
        interface Invalid {
          long error(int index);
        }
        """);

    var c = compile(missing, invalid);

    assertThat(c).hadErrorContaining(
        "@CallState interface must declare exactly one abstract accessor");
    assertThat(c).hadErrorContaining(
        "@CallState accessor must have no parameters and return int");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void rejectsMultipleCallStateParameters() {
    var source = forSourceString("test.Multiple", """
        package test;

        @org.alveolo.ffm.CallState("errno")
        interface ErrorState {
          int error();
        }

        @org.alveolo.ffm.ForeignInterface
        interface Multiple {
          void call(ErrorState first, ErrorState second);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Only one @CallState parameter is allowed");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void rejectsCallStateReturn() {
    var source = forSourceString("test.ReturnsState", """
        package test;

        @org.alveolo.ffm.CallState("errno")
        interface ErrorState {
          int error();
        }

        @org.alveolo.ffm.ForeignInterface
        interface ReturnsState {
          ErrorState call();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@CallState types are only supported as parameters");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void selectsFirstMatchingPlatformOverride() {
    var osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    var os = osName.contains("mac") || osName.contains("darwin")
        ? Library.OS.MACOS
        : osName.contains("win") ? Library.OS.WINDOWS : Library.OS.LINUX;

    assertEquals("selected", ForeignUtils.callStateName(
        "fallback",
        new ForeignUtils.CallStateOverride(
            new Library.OS[] {os}, "selected"),
        new ForeignUtils.CallStateOverride(
            new Library.OS[] {os}, "later")));
  }

  @Test
  void recognizesPreviouslyGeneratedCallStateClass() {
    var state = forSourceString("test.NativeError", """
        package test;
        final class NativeError {
          static final java.lang.foreign.Linker.Option LinkerOption$F = null;
          final java.lang.foreign.MemorySegment MemorySegment$F = null;
        }
        """);
    var api = forSourceString("test.NativeApi", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        interface NativeApi {
          int call(NativeError capture, int value);
        }
        """);

    var c = compile(state, api);

    assertThat(c).succeeded();
  }
}
