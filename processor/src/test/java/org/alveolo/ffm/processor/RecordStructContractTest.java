package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/// Record structs must stay snapshots. These are intentional rejections, not
/// missing conversion features to implement with more lifetime management.
class RecordStructContractTest extends AbstractProcessorTest {
  static final String MEMORY_BACKED_COMPONENT =
      "Record structs cannot contain memory-backed types;"
          + " use a record struct component or declare the containing"
          + " struct as an interface";

  static Stream<String> passModes() {
    return Stream.of("", "@Value", "@Address");
  }

  static Stream<Arguments> memoryBackedComponents() {
    return Stream.of("@Struct", "@Union", "@CallState(\"errno\")")
        .flatMap(annotation -> passModes()
            .map(passMode -> Arguments.of(annotation, passMode)));
  }

  @ParameterizedTest(name = "{0}, pass mode: [{1}]")
  @MethodSource("memoryBackedComponents")
  void rejectsMemoryBackedComponentsRegardlessOfPassMode(
      String annotation, String passMode) {
    var source = forSourceString("test.Snapshot", """
        package test;
        import org.alveolo.ffm.*;

        %s interface View { int value(); }
        @Struct record Snapshot(%s View value) {}
        """.formatted(annotation, passMode));

    var compilation = compile(source);

    assertThat(compilation).failed();
    assertThat(compilation).hadErrorCount(1);
    assertEquals(MEMORY_BACKED_COMPONENT,
        compilation.errors().getFirst().getMessage(Locale.ROOT));
  }

  @ParameterizedTest(name = "pass mode: [{0}]")
  @MethodSource("passModes")
  void rejectsGeneratedWrapperComponentsRegardlessOfPassMode(String passMode) {
    var source = forSourceString("test.Snapshot", """
        package test;
        import org.alveolo.ffm.*;

        @Struct(name = "NativeView") interface View { int value(); }
        @Struct record Snapshot(%s NativeView value) {}
        """.formatted(passMode));

    var compilation = compile(source);

    assertThat(compilation).failed();
    assertThat(compilation).hadErrorCount(1);
    assertEquals(MEMORY_BACKED_COMPONENT,
        compilation.errors().getFirst().getMessage(Locale.ROOT));
  }

  @ParameterizedTest(name = "pass mode: [{0}]")
  @MethodSource("passModes")
  void rejectsMemoryBackedArrayElementsRegardlessOfPassMode(String passMode) {
    var source = forSourceString("test.Snapshot", """
        package test;
        import org.alveolo.ffm.*;

        @Struct interface View { int value(); }
        @Struct record Snapshot(@Sequence(2) %s View[] values) {}
        """.formatted(passMode));

    var compilation = compile(source);

    assertThat(compilation).failed();
    assertEquals("Record arrays support primitives and"
        + " value-style @Struct record elements only",
        compilation.errors().getFirst().getMessage(Locale.ROOT));
  }

  @Test
  void rejectsViewsInsideNestedRecordsAndCallArraySnapshots() {
    var source = forSourceString("test.Snapshots", """
        package test;
        import org.alveolo.ffm.*;

        @Struct interface View { int value(); }
        @Struct record Inner(@Value View value) {}
        @Struct record Outer(Inner inner) {}
        @Struct record Batch(@Sequence(2) Outer[] values) {}

        @ForeignInterface interface Snapshots {
          Outer get();
          Batch batch();
          void fill(@Out Outer[] values);
        }
        """);

    var compilation = compile(source);

    assertThat(compilation).failed();
    assertThat(compilation).hadErrorCount(1);
    assertEquals(MEMORY_BACKED_COMPONENT,
        compilation.errors().getFirst().getMessage(Locale.ROOT));
  }

  @ParameterizedTest
  @ValueSource(strings = {"BinaryMemoryStruct", "BinaryMemoryWrapper"})
  void rejectsMemoryBackedTypesFromClassFiles(String type) {
    var source = forSourceString(
        "org.alveolo.ffm.processor.fixture.Snapshot", """
            package org.alveolo.ffm.processor.fixture;
            import org.alveolo.ffm.*;

            @Struct record Snapshot(@Value %s value) {}
            """.formatted(type));

    var compilation = compile(source);

    assertThat(compilation).failed();
    assertThat(compilation).hadErrorCount(1);
    assertEquals(MEMORY_BACKED_COMPONENT,
        compilation.errors().getFirst().getMessage(Locale.ROOT));
  }
}
