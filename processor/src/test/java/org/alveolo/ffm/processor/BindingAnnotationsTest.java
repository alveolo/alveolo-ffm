package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class BindingAnnotationsTest extends AbstractProcessorTest {
  private static final String IMPORTS = """
      package test;
      import java.lang.annotation.*;
      import java.lang.foreign.MemorySegment;
      import org.alveolo.ffm.*;
      import org.alveolo.ffm.macos.*;
      """;

  @Test
  void generatesComposedReleaseExactlyLikeDirectRelease() {
    var direct = compile(forSourceString("test.Lib", IMPORTS + """
        @Struct record Pair(int left, int right) {}
        interface Support { void free(MemorySegment pointer); }
        @ForeignInterface
        interface Lib extends Support {
          @Require(Support.class) @Release("free") String text();
          @Release("free") String copy(@Out int[] values);
          @Address @Require(Support.class) @Release("free") Pair pair();
        }
        """));
    var composed = compile(forSourceString("test.Lib", IMPORTS + """
        @Struct record Pair(int left, int right) {}
        interface Support { void free(MemorySegment pointer); }
        @Require(Support.class) @Release("free")
        @Target(ElementType.TYPE_USE) @Retention(RetentionPolicy.CLASS)
        @interface Owned {}
        @Owned
        @Target(ElementType.TYPE_USE) @Retention(RetentionPolicy.CLASS)
        @interface OwnedAgain {}
        @ForeignInterface
        interface Lib extends Support {
          @Owned @OwnedAgain String text();
          @Owned String copy(@Out int[] values);
          @Address @Owned Pair pair();
        }
        """));
    assertThat(direct).succeeded();
    assertThat(composed).succeeded();
    assertThat(composed).generatedSourceFile("test.LibFFM")
        .hasSourceEquivalentTo(direct.generatedSourceFile("test.LibFFM").orElseThrow());
  }

  @Test
  void generatesCFReleaseIndependentlyAndThroughOwnedComposition() {
    for (var conversion : new String[] {"", "@CFString "}) {
      var direct = compile(forSourceString("test.Lib", IMPORTS + """
          @ForeignInterface interface Lib extends CFSupport {
          """ + conversion + """
            @Release("CFRelease") String text();
          }
          """));
      var composed = compile(forSourceString("test.Lib", IMPORTS + """
          @ForeignInterface interface Lib extends CFSupport {
          """ + conversion + """
            @CFRelease String text();
          }
          """));
      assertThat(direct).succeeded();
      assertThat(composed).succeeded();
      assertThat(composed).generatedSourceFile("test.LibFFM")
          .hasSourceEquivalentTo(direct.generatedSourceFile("test.LibFFM").orElseThrow());
      if (!conversion.isEmpty()) {
        var owned = compile(forSourceString("test.Lib", IMPORTS + """
            @ForeignInterface interface Lib extends CFSupport {
              @CFStringOwned String text();
            }
            """));
        assertThat(owned).succeeded();
        assertThat(owned).generatedSourceFile("test.LibFFM")
            .hasSourceEquivalentTo(direct.generatedSourceFile("test.LibFFM").orElseThrow());
      }
    }
  }

  @Test
  void rejectsCFReleaseWithoutSupportEvenWhenReleaseMethodExists() {
    fails("""
        @ForeignInterface interface Lib {
          @CFRelease String text();
          void CFRelease(MemorySegment pointer);
        }
        """, "requires org.alveolo.ffm.macos.CFSupport");
  }

  @Test
  void resolvesBinaryCompositionAndRequirementsOnActualBinding() {
    var inherited = compile(forSourceString("test.Lib", IMPORTS + """
        import org.alveolo.ffm.processor.fixture.OwnedText;
        interface Text { @OwnedText String text(); }
        @ForeignInterface
        interface Lib extends Text, OwnedText.Support {}
        """));
    var direct = compile(forSourceString("test.Lib", IMPORTS + """
        import org.alveolo.ffm.processor.fixture.OwnedText;
        interface Text { @Release("free") String text(); }
        @ForeignInterface
        interface Lib extends Text, OwnedText.Support {}
        """));
    assertThat(inherited).succeeded();
    assertThat(direct).succeeded();
    assertThat(inherited).generatedSourceFile("test.LibFFM")
        .hasSourceEquivalentTo(direct.generatedSourceFile("test.LibFFM").orElseThrow());
  }

  @Test
  void mergesRequirementsAndAcceptsTransitiveInterfaces() {
    var compilation = compile(forSourceString("test.Lib", IMPORTS + """
        interface A {}
        interface B {}
        interface Both extends A, B {}
        @Require(A.class) @Target(ElementType.TYPE_USE) @interface NeedsA {}
        @Require(B.class) @Target(ElementType.TYPE_USE) @interface NeedsB {}
        @NeedsA @NeedsB
        @ForeignInterface
        interface Lib extends Both { int call(); }
        """));
    assertThat(compilation).succeeded();
  }

  @Test
  void rejectsMissingComposedRequirementEvenWhenMethodExists() {
    fails("""
        import org.alveolo.ffm.processor.fixture.OwnedText;
        @ForeignInterface
        interface Lib {
          @OwnedText String text();
          void free(MemorySegment pointer);
        }
        """, "does not extend it");
  }

  @Test
  void rejectsNonInterfaceRequirement() {
    fails("""
        @ForeignInterface @Require(String.class)
        interface Lib {}
        """, "@Require must name an interface");
  }

  @Test
  void rejectsConflictingReleaseMethods() {
    fails("""
        @Release("first") @Target(ElementType.TYPE_USE) @interface Owned {}
        @ForeignInterface
        interface Lib {
          @Owned @Release("second") String text();
          void first(MemorySegment pointer);
          void second(MemorySegment pointer);
        }
        """, "Conflicting @Release methods");
  }

  @Test
  void qualifiesReleaseMethodNamedYield() {
    assertThat(compile(forSourceString("test.Lib", IMPORTS + """
        @ForeignInterface interface Lib {
          @Release("yield") String text();
          void yield(MemorySegment pointer);
        }
        """))).succeeded();
  }

  @Test
  void rejectsWrongReleaseMethodSignature() {
    for (var method : new String[] {
        "", "int free(MemorySegment p);", "void free(long p);",
        "static void free(MemorySegment p) {}", "private void free(MemorySegment p) {}"
    }) {
      fails("""
          @ForeignInterface interface Lib {
            @Release("free") String text();
          """ + method + "}", "@Release requires an instance method void free(MemorySegment)");
    }
  }

  @Test
  void rejectsReleaseOnEscapingPointersAndOtherReturns() {
    for (var type : new String[] {
        "MemorySegment", "int", "Pair", "@Value Pair", "View", "@Value View"
    }) {
      fails("""
          @Struct record Pair(int left, int right) {}
          @Struct interface View { int value(); }
          @ForeignInterface interface Lib {
            void free(MemorySegment p);
            @Release("free")
          """ + type + " call(); }", "@Release is only supported on String or @Address record return types");
    }
  }

  @Test
  void rejectsReleaseOnParametersAndFields() {
    fails("""
        @ForeignInterface interface Lib {
          void call(@Release("free") String text);
        }
        """, "@Release is only supported on String or @Address record return types");
    fails("""
        @Struct interface Lib {
          @Release("free") String text();
        }
        """, "not fields");
    fails("""
        @ForeignInterface interface Lib {
          void call(@Release("free") int[] values);
        }
        """, "not supported on nested type positions");
  }

  @Test
  void rejectsReleaseOnSyntheticAllocatorParameters() {
    fails("""
        import java.lang.foreign.SegmentAllocator;
        @Struct interface Pair { int value(); }
        @ForeignInterface interface Lib {
          @Value Pair pair(@Release("free") SegmentAllocator allocator);
        }
        """, "@Release is only supported on String or @Address record return types");
  }

  @Test
  void rejectsCyclesButIgnoresUnrelatedAnnotationCycles() {
    fails("""
        @B @Release("free") @Target({ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE})
        @interface A {}
        @A @Target({ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE}) @interface B {}
        @ForeignInterface interface Lib {
          @A String text();
          void free(MemorySegment p);
        }
        """, "Cyclic binding annotation composition");
    assertThat(compile(forSourceString("test.Lib", IMPORTS + """
        @B @Target(ElementType.TYPE_USE) @interface A {}
        @A @Target(ElementType.TYPE_USE) @interface B {}
        @ForeignInterface interface Lib { @A int call(); }
        """))).succeeded();
  }

  @Test
  void rejectsSourceOnlyAndParameterizedBundles() {
    fails("""
        @Release("free") @Target(ElementType.TYPE_USE) @Retention(RetentionPolicy.SOURCE)
        @interface Owned {}
        @ForeignInterface interface Lib {
          @Owned String text(); void free(MemorySegment p);
        }
        """, "require CLASS or RUNTIME retention");
    fails("""
        @Release("free") @Target(ElementType.TYPE_USE)
        @interface Owned { String value(); }
        @ForeignInterface interface Lib {
          @Owned("other") String text(); void free(MemorySegment p);
        }
        """, "must not declare elements");
  }

  @Test
  void borrowedCFStringStillRequiresSupport() {
    fails("""
        @ForeignInterface interface Lib { @CFString String text(); }
        """, "requires org.alveolo.ffm.macos.CFSupport");
  }

  @Test
  void dispatchCallsUseTheSameComposition() {
    var direct = compile(forSourceString("test.Lib", IMPORTS + """
        @DispatchTable interface Lib {
          @Slot(0) @Release("free") String text();
          @Slot(1) void free(MemorySegment p);
        }
        """));
    var composed = compile(forSourceString("test.Lib", IMPORTS + """
        @Release("free") @Target(ElementType.TYPE_USE) @interface Owned {}
        @DispatchTable interface Lib {
          @Slot(0) @Owned String text();
          @Slot(1) void free(MemorySegment p);
        }
        """));
    assertThat(direct).succeeded();
    assertThat(composed).succeeded();
    assertThat(composed).generatedSourceFile("test.LibFD")
        .hasSourceEquivalentTo(direct.generatedSourceFile("test.LibFD").orElseThrow());
  }

  @Test
  void checksRequirementsInheritedFromSupportInterfaces() {
    fails("""
        interface Required {}
        @Require(Required.class) interface Support { int call(); }
        @ForeignInterface interface Lib extends Support {}
        """, "does not extend it");
    assertThat(compile(forSourceString("test.Lib", IMPORTS + """
        interface Required {}
        @Require(Required.class) interface Support { int call(); }
        @ForeignInterface interface Lib extends Support, Required {}
        """))).succeeded();
  }

  @Test
  void rejectsReleaseOnBindingDeclarationsAndNestedFields() {
    fails("""
        @Release("free") @ForeignInterface interface Lib {}
        """, "not supported on binding declarations");
    fails("""
        @Struct record Lib(@Sequence(2) @Release("free") int[] values) {}
        """, "not fields");
  }

  @Test
  void preservesCompositionAndReceiverThroughVirtualBridges() {
    for (var result : new String[] {"String", "@Address Pair"}) {
      var support = """
          @Struct record Pair(int value, MemorySegment pointer) {}
          interface Support {
            default void free(MemorySegment pointer) {}
          }
          @Require(Support.class) @Release("free")
          @Target(ElementType.TYPE_USE) @interface Owned {}
          @Struct(vtable = true) interface Lib extends Support {
            @Virtual(0) @Owned <result> text();
          }
          """.replace("<result>", result);
      var composed = compile(forSourceString("test.Lib", IMPORTS + support));
      var direct = compile(forSourceString("test.Lib", IMPORTS + support.replace(
          "@Virtual(0) @Owned", "@Virtual(0) @Require(Support.class) @Release(\"free\")")));
      assertThat(composed).succeeded();
      assertThat(direct).succeeded();
      var methodAnnotation = compile(forSourceString("test.Lib", IMPORTS + support.replace(
          "@Target(ElementType.TYPE_USE)", "@Target(ElementType.METHOD)")));
      assertThat(methodAnnotation).succeeded();
      for (var generated : new String[] {"test.LibFM", "test.LibVtbl", "test.LibVtblFD"}) {
        assertThat(methodAnnotation).generatedSourceFile(generated)
            .hasSourceEquivalentTo(direct.generatedSourceFile(generated).orElseThrow());
        assertThat(composed).generatedSourceFile(generated)
            .hasSourceEquivalentTo(direct.generatedSourceFile(generated).orElseThrow());
      }
    }
  }

  private void fails(String source, String error) {
    assertThat(compile(forSourceString("test.Lib", IMPORTS + source)))
        .hadErrorContaining(error);
  }
}
