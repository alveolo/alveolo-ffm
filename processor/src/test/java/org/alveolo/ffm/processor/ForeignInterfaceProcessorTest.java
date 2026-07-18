package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class ForeignInterfaceProcessorTest extends AbstractProcessorTest {
  @Test
  void stripsSpecFromForeignInterfaceNames() {
    var source = forSourceString("test.LibrarySpec", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface LibrarySpec {}

        class UseLibrary {
          Library value;
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void rejectsReservedCopiedParameterSuffix() {
    var source = forSourceString("test.BadParameter", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface BadParameter {
          void call(int result$f);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "User parameter names ending in '$F' or '$f' are reserved");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void generatesEmptyFFM() {
    var c = compile("interface/Empty.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.EmptyFFM", "interface/EmptyFFM.java");
  }

  @Test
  void generatesNameOverrideFFM() {
    var c = compile("interface/LibraryApi.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.RenamedLibraryFFM",
        "interface/RenamedLibraryFFM.java");
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
  void rejectsFirstVariadicArgOutsideNativeParameterRange() {
    var lib = forSourceString("test.BadVariadic", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface BadVariadic {
          @org.alveolo.ffm.FirstVariadicArg(-1)
          void negative();

          @org.alveolo.ffm.FirstVariadicArg(2)
          void tooHigh(int value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@FirstVariadicArg value must be between 0 and 0");
    assertThat(c).hadErrorContaining(
        "@FirstVariadicArg value must be between 0 and 1");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void rejectsUnpromotedVariadicParameterTypes() {
    var lib = forSourceString("test.BadVariadic", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface BadVariadic {
          @org.alveolo.ffm.FirstVariadicArg(1)
          void bad(short fixed, short integer, float decimal,
              @org.alveolo.ffm.WCharT int wide);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "Variadic parameter 'integer' must use its C-promoted type: "
            + "use int instead of short");
    assertThat(c).hadErrorContaining(
        "Variadic parameter 'decimal' must use its C-promoted type: "
            + "use double instead of float");
    assertThat(c).hadErrorContaining(
        "Variadic parameter 'wide' must use its C-promoted type: "
            + "remove @WCharT and use plain int");
    assertThat(c).hadErrorCount(3);
  }

  @Test
  void rejectsInvalidCanonicalScalarTypes() {
    var lib = forSourceString("test.BadCanonicalScalars", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface BadCanonicalScalars {
          void cLong(@org.alveolo.ffm.CLong int value);
          void sizeT(@org.alveolo.ffm.SizeT int value);
          void wchar(@org.alveolo.ffm.WCharT long value);
          void conflicting(
              @org.alveolo.ffm.CLong @org.alveolo.ffm.SizeT long value);
          void array(@org.alveolo.ffm.CLong long[] values);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("@CLong requires Java long");
    assertThat(c).hadErrorContaining("@SizeT requires Java long");
    assertThat(c).hadErrorContaining("@WCharT requires Java int");
    assertThat(c).hadErrorContaining(
        "Only one of @CLong, @SizeT, and @WCharT may be used on a type");
    assertThat(c).hadErrorContaining(
        "@CLong is only supported on scalar values");
    assertThat(c).hadErrorCount(5);
  }

  @Test
  void generatesArrayParameterFFM() {
    var c = compile("interface/arrays/ArrayParameters.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ArrayParametersFFM",
        "interface/arrays/ArrayParametersFFM.java");
  }

  @Test
  void failsWhenValueArrayOrBufferHasNoFixedSequence() {
    var lib = forSourceString("test.Lib", """
        package test;

        import java.nio.IntBuffer;
        import org.alveolo.ffm.ForeignInterface;
        import org.alveolo.ffm.Value;

        @ForeignInterface
        public interface Lib {
          void array(@Value int[] values);
          void buffer(@Value IntBuffer values);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@Value array and Buffer parameters require @Sequence");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void failsWhenValueArrayIsOutputOnly() {
    var lib = forSourceString("test.Lib", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void array(
              @org.alveolo.ffm.Value
              @org.alveolo.ffm.Out
              @org.alveolo.ffm.Sequence(2) int[] values);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@Out is not supported on @Value array and Buffer parameters");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByNamesNoParameter() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.CountedBy("missing") int[] values,
              int count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CountedBy(\"missing\") does not name a parameter");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByNameIsBlank() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.CountedBy("") int[] values,
              int count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CountedBy(\"\") does not name a parameter");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByNamesUnsupportedCountType() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.CountedBy("count") int[] values,
              float count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "must be a plain scalar of type byte, short, int, or long");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByNamesAddressCount() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.CountedBy("count") int[] values,
              @org.alveolo.ffm.Address int count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "must be a plain scalar of type byte, short, int, or long");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByAndSequenceAreCombined() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(
              @org.alveolo.ffm.CountedBy("count")
              @org.alveolo.ffm.Sequence(4) int[] values,
              int count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CountedBy and @Sequence cannot be used together");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenCountedByIsUsedOnScalar() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.CountedBy("count") int value,
              int count);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@CountedBy is only supported on primitive arrays");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsNonPositiveCallSequence() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.Sequence(0) int[] values);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("@Sequence value must be positive");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsArrayAndBufferReturnsWithFocusedDiagnostic() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          int[] array();
          java.nio.IntBuffer buffer();
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "Array and Buffer return types are not supported");
    assertThat(c).hadErrorCount(2);
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
  void failsWhenLibraryValueIsMissing() {
    var lib = forSourceString("test.Lib", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        @org.alveolo.ffm.Library
        public interface Lib {}
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("@Library value is required");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsWhenMemorySegmentWrapperReturnHasNoAllocator() {
    var struct = forSourceString("test.Div", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Div {
          int quot();
          int rem();
        }
        """);

    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.Symbol("div")
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
        @org.alveolo.ffm.Struct
        public interface Div {
          int quot();
          int rem();
        }
        """);

    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.Symbol("div")
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
  void failsWhenSequenceIsUsedOnScalarParameter() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.Sequence(2) int value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining(
        "@Sequence is only supported on array and Buffer types");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void supportsMemorySegmentAddressReturn() {
    var lib = forSourceString("test.Lib", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          @org.alveolo.ffm.Symbol("strchr")
          java.lang.foreign.MemorySegment strchr(String string, int ch);
        }
        """);

    var c = compile(lib);

    assertThat(c).succeeded();
  }

  @Test
  void failsNonForeignMemoryClassParameter() {
    var lib = forSourceString("test.Lib", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(Object value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsAddressOnUnsupportedClassParameter() {
    var lib = forSourceString("test.Lib", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        public interface Lib {
          void f(@org.alveolo.ffm.Address Object value);
        }
        """);

    var c = compile(lib);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
    assertThat(c).hadErrorCount(1);
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

  @Test
  void failsNestedForeignInterface() {
    var source = forSourceString("test.Outer", """
        package test;
        class Outer {
          @org.alveolo.ffm.ForeignInterface
          interface Lib {
            void f();
          }
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Nested @ForeignInterface types are not yet supported");
    assertThat(c).hadErrorCount(1);
  }
}
