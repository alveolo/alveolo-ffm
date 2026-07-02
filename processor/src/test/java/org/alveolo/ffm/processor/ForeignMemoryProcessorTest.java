package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import org.junit.jupiter.api.Test;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @Test
  void generatesStructClass() {
    var c = compile("memory/struct/timeval.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.timevalFM", "memory/struct/timevalFM.java");
  }

  @Test
  void generatesMultipleStructs() {
    var c = compile(
        "memory/struct/multiple/StructA.java",
        "memory/struct/multiple/StructB.java");
    assertThat(c).succeeded();
    assertGenerated(c,
        "pkg.StructAFM", "memory/struct/multiple/StructAFM.java");
    assertGenerated(c,
        "pkg.StructBFM", "memory/struct/multiple/StructBFM.java");
  }

  @Test
  void generatesUnionClass() {
    var c = compile("memory/union/Union.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.UnionFM", "memory/union/UnionFM.java");
  }

  @Test
  void generatesNestedStruct() {
    var c = compile("memory/nested/Inner.java", "memory/nested/Outer.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.InnerFM", "memory/nested/InnerFM.java");
    assertGenerated(c, "pkg.OuterFM", "memory/nested/OuterFM.java");
  }

  @Test
  void generatesPassModeFields() {
    var c = compile("memory/passmode/FieldModes.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.FieldModesFM",
        "memory/passmode/FieldModesFM.java");
    assertGenerated(c, "pkg.FieldModeAccessorsFM",
        "memory/passmode/FieldModeAccessorsFM.java");
  }

  @Test
  void generatesObjectVtblStruct() {
    var c = compile(
        "memory/object/NativeApi.java",
        "memory/object/Obj.java",
        "memory/object/VirtualObj.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ObjFM", "memory/object/ObjFM.java");
    assertGenerated(c, "pkg.VirtualObjFM",
        "memory/object/VirtualObjFM.java");
    assertGenerated(c, "pkg.VirtualObjVtbl",
        "memory/object/VirtualObjVtbl.java");
    assertGenerated(c, "pkg.VirtualObjVtblFD",
        "memory/object/VirtualObjVtblFD.java");
  }

  @Test
  void failsStructOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.Struct
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@Struct can only be applied to an interface, not ENUM");
  }

  @Test
  void failsUnionOnEnum() {
    var source = forSourceString("test.BadEnum", """
        package test;
        @org.alveolo.ffm.Union
        public enum BadEnum { A, B }
        """);
    var c = compile(source);
    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not ENUM");
  }

  @Test
  void failsStructOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.Struct
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct can only be applied to an interface, not CLASS");
  }

  @Test
  void failsUnionOnClass() {
    var source = forSourceString("test.BadClass", """
        package test;
        @org.alveolo.ffm.Union
        public abstract class BadClass {public int x();}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not CLASS");
  }

  @Test
  void failsUnionOnRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Union
        public record BadRecord(int a) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union can only be applied to an interface, not RECORD");
  }

  @Test
  void failsVtableStructOnRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public record BadRecord(int a) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct(vtable = true) can only be applied to an interface, not RECORD");
  }

  @Test
  void failsVirtualOnNonVtableStruct() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          @org.alveolo.ffm.Virtual(1) int bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Virtual is only allowed on @Struct(vtable = true) methods");
  }

  @Test
  void failsDuplicateVirtualSlot() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Bad {
          @org.alveolo.ffm.Virtual(1) int a();
          @org.alveolo.ffm.Virtual(1) int b();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Duplicate @Virtual slot: 1");
  }

  @Test
  void failsNegativeVirtualSlot() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Bad {
          @org.alveolo.ffm.Virtual(-1) int bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Virtual value must be non-negative");
  }

  @Test
  void failsVirtualAndSymbolOnSameMethod() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        interface NativeApi {}
        @org.alveolo.ffm.Struct(vtable = true, symbols = NativeApi.class)
        public interface Bad {
          @org.alveolo.ffm.Virtual(1)
          @org.alveolo.ffm.Symbol("bad")
          int bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Virtual and @Symbol cannot be used on the same method");
  }

  @Test
  void failsSymbolMethodWithoutSymbolsOwner() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          @org.alveolo.ffm.Symbol("bad") int bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct symbols is required when @Symbol methods are used");
  }

  @Test
  void failsSymbolOwnerWithoutForeignInterface() {
    var source = forSourceString("test.Bad", """
        package test;
        interface NativeApi {}
        @org.alveolo.ffm.Struct(symbols = NativeApi.class)
        public interface Bad {
          @org.alveolo.ffm.Symbol("bad") int bad();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct symbols must reference an @ForeignInterface");
  }

  @Test
  void failsReservedVtblMemberName() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Bad {
          int ff$vtbl();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "'ff$vtbl' is reserved for generated vtable access");
  }

  @Test
  void failsUnsupportedObjectMethodType() {
    var nativeApi = forSourceString("test.NativeApi", """
        package test;
        @org.alveolo.ffm.ForeignInterface
        public interface NativeApi {}
        """);
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(symbols = NativeApi.class)
        public interface Bad {
          @org.alveolo.ffm.Symbol("bad") Object bad();
        }
        """);

    var c = compile(nativeApi, source);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
  }

  @Test
  void failsStructOnRecordWithUnsupportedType() {
    var source = forSourceString("test.BadRecord", """
        package test;
        import org.alveolo.ffm.Struct;
        import java.util.List;
        @Struct
        public record BadRecord(List<String> data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Type is not supported: java.util.List");
  }

  @Test
  void failsStructFieldWithoutGetter() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          Bad x(int value);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining("Field 'x' has no accessor");
  }

  @Test
  void failsArrayFieldOnStructInterface() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          int[] data();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Array fields are not supported, use java.nio.IntBuffer instead");
  }

  @Test
  void failsArrayFieldOnStructRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Struct
        public record BadRecord(double[] data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Array fields are not supported, use java.nio.DoubleBuffer instead");
  }

  @Test
  void failsBufferFieldOnStructRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Struct
        public record BadRecord(java.nio.IntBuffer data) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Buffer fields are not supported on records");
  }
}
