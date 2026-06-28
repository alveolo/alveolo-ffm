package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;

import org.junit.jupiter.api.Test;

class ForeignInterfaceProcessorTest extends AbstractProcessorTest {
  @Test
  void x() {
    Linker.nativeLinker().canonicalLayouts()
        .forEach((name, value) -> IO.println("type: " + name
            + ",\tvalue: " + value.toString()
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
    var c = compile("interface/LibC.java",
        "value/div_t.java", "value/ldiv_t.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.LibCFFM", "interface/LibCFFM.java");
    assertGenerated(c, "pkg.div_tFM", "value/div_tFM.java");
    assertGenerated(c, "pkg.ldiv_tFM", "value/ldiv_tFM.java");
  }

  @Test
  void generatesPassModeFFM() {
    var c = compile("interface/passmode/PassMode.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.PassModeFFM",
        "interface/passmode/PassModeFFM.java");
  }

  @Test
  void generatesArrayParameterFFM() {
    var c = compile("interface/arrays/ArrayParameters.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ArrayParametersFFM",
        "interface/arrays/ArrayParametersFFM.java");
  }

  @Test
  void generatesMacFrameworkFFM() {
    var c = compile("interface/CoreFramework.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.CoreFrameworkFFM",
        "interface/CoreFrameworkFFM.java");
  }

  @Test
  void generatesCFStringFFM() {
    var c = compile("interface/CoreStrings.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.CoreStringsFFM",
        "interface/CoreStringsFFM.java");
  }

  @Test
  void generatesLibraryFFM() {
    var c = compile("interface/NativeTest.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.NativeTestFFM",
        "interface/NativeTestFFM.java");
  }

  @Test
  void generatesLibraryLookupFFM() {
    var c = compile("interface/NativeLookupTest.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.NativeLookupTestFFM",
        "interface/NativeLookupTestFFM.java");
  }

  @Test
  void generatesRepeatableLibraryFFM() {
    var c = compile("interface/MultiLibrary.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.MultiLibraryFFM",
        "interface/MultiLibraryFFM.java");
  }

  @Test
  void generatesMultiMacFrameworkFFM() {
    var c = compile("interface/MultiFramework.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.MultiFrameworkFFM",
        "interface/MultiFrameworkFFM.java");
  }

  @Test
  void failsWhenMemorySegmentWrapperReturnHasNoAllocator() {
    var struct = forSourceString("test.Div", """
        package test;
        @org.alveolo.ffm.ForeignStruct
        public interface Div {
          int quot();
          int rem();
        }
        """);

    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.ForeignName("div")
          @org.alveolo.ffm.Value Div div(int numerator, int denominator);
        }
        """);

    var c = compile(struct, lib);

    assertThat(c).hadErrorContaining("SegmentAllocator is expected");
  }

  @Test
  void usesSeparateAllocatorsForWrapperReturnAndConvertedParameters() {
    var struct = forSourceString("test.Div", """
        package test;
        @org.alveolo.ffm.ForeignStruct
        public interface Div {
          int quot();
          int rem();
        }
        """);

    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.ForeignName("div")
          @org.alveolo.ffm.Value Div div(
              java.lang.foreign.SegmentAllocator allocator, String label);
        }
        """);

    var c = compile(struct, lib);

    assertThat(c).succeeded();
  }

  @Test
  void failsWhenCFStringIsUsedOnNonString() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.macos.CFString int value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CFString is only supported on java.lang.String");
  }

  @Test
  void failsWhenOwnedCFStringIsUsedOnParameter() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.macos.CFString(owned = true) String value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CFString(owned = true) is only supported on return types");
  }

  @Test
  void failsWhenInAndOutAreUsedTogether() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.In @org.alveolo.ffm.Out int[] values);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("@In and @Out cannot be used together");
  }

  @Test
  void failsWhenInOrOutIsUsedOnScalarParameter() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.Out int value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@In and @Out are only supported on array and Buffer parameters");
  }

  @Test
  void supportsMemorySegmentAddressReturn() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.ForeignName("strchr")
          java.lang.foreign.MemorySegment strchr(String string, int ch);
        }
        """);

    var c = compile(lib);

    assertThat(c).succeeded();
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
