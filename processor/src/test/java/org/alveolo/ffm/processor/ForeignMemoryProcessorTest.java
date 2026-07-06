package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @Test
  @Disabled
  void printNativeLinkerCanonicalLayouts() {
    Linker.nativeLinker().canonicalLayouts()
        .forEach((name, value) -> IO.println("type: " + name
            + ",\tvalue: " + value.toString()
            + ",\tbyteSize: " + value.byteSize()
            + ",\tbyteAlignment: " + value.byteAlignment()
            + ",\tbyteOffset: " + value.byteOffset()));
  }

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
  void generatesBufferStructField() {
    var c = compile("memory/buffer/BufferStruct.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.BufferStructFM",
        "memory/buffer/BufferStructFM.java");
  }

  @Test
  void failsAddressRecordFieldOnInterfaceStruct() {
    var source = forSourceString("test.BadStruct", """
        package test;

        @org.alveolo.ffm.Struct
        record Pair(int left, int right) {}

        @org.alveolo.ffm.Struct
        interface BadStruct {
          @org.alveolo.ffm.Address Pair pair();
        }
        """);
    var generatedUse = forSourceString("test.BadStructUse", """
        package test;

        class BadStructUse {
          Class<?> generatedClass = BadStructFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "@Address record fields are not supported on memory-backed "
            + "@Struct or @Union interfaces");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void generatesTransitiveRecordAddressValueField() {
    var c = compile("memory/address/TransitiveRecords.java");

    assertThat(c).succeeded();
    assertGenerated(c, "pkg.PairBoxFM", "memory/address/PairBoxFM.java");
    assertGenerated(c, "pkg.IntBoxFM", "memory/address/IntBoxFM.java");
    assertGenerated(c, "pkg.OuterFM", "memory/address/OuterFM.java");
  }

  @Test
  void failsStringFieldOnMemoryStruct() {
    var source = forSourceString("test.BadStruct", """
        package test;

        @org.alveolo.ffm.Struct
        record BadStruct(String value) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "String fields are not supported on @Struct or @Union memory types");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsAddressPrimitiveFieldOnInterfaceStruct() {
    var source = forSourceString("test.BadStruct", """
        package test;

        @org.alveolo.ffm.Struct
        interface BadStruct {
          @org.alveolo.ffm.Address long pointer();
        }
        """);
    var generatedUse = forSourceString("test.BadStructUse", """
        package test;

        class BadStructUse {
          Class<?> generatedClass = BadStructFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "@Address primitive fields are not supported on memory-backed "
            + "@Struct or @Union interfaces");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void generatesSimpleNameOverridesInSourcePackage() {
    var c = compile("memory/override/SimpleOverrides.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.RenamedPoint",
        "memory/override/RenamedPoint.java");
    assertGenerated(c, "pkg.RenamedChoice",
        "memory/override/RenamedChoice.java");
  }

  @Test
  void failsQualifiedStructNameOverride() {
    var source = forSourceString("test.BadStruct", """
        package test;
        @org.alveolo.ffm.Struct(name = "other.BadStructFM")
        public interface BadStruct {
          int value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Struct name must be a simple Java class name");
  }

  @Test
  void failsQualifiedUnionNameOverride() {
    var source = forSourceString("test.BadUnion", """
        package test;
        @org.alveolo.ffm.Union(name = "other.BadUnionFM")
        public interface BadUnion {
          int value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union name must be a simple Java class name");
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
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(nativeApi, source, generatedUse);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnsupportedVirtualMethodTypeWithoutGeneratedCompileErrors() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Bad {
          @org.alveolo.ffm.Virtual(0) Object bad();
        }
        """);
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining("Type is not supported: java.lang.Object");
    assertThat(c).hadErrorCount(1);
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
    var generatedUse = forSourceString("test.BadRecordUse", """
        package test;

        class BadRecordUse {
          Class<?> generatedClass = BadRecordFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining("Type is not supported: java.util.List");
    assertThat(c).hadErrorCount(1);
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
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining("Field 'x' has no accessor");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnsupportedStructSetterWithoutGeneratedCompileErrors() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          int x();
          void x(int value);
        }
        """);
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Unsupported accessor signature for field 'x'");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnsupportedNestedRecordSetterWithoutGeneratedCompileErrors() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        record Pair(int x) {}

        @org.alveolo.ffm.Struct
        public interface Bad {
          Pair pair();
          void pair(Pair value);
        }
        """);
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Unsupported accessor signature for field 'pair'");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnsupportedBufferOverloadWithoutGeneratedCompileErrors() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          @org.alveolo.ffm.Sequence(3)
          java.nio.IntBuffer data();
          void data(int value);
        }
        """);
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Unsupported accessor signature for field 'data'");
    assertThat(c).hadErrorCount(1);
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
    var generatedUse = forSourceString("test.BadUse", """
        package test;

        class BadUse {
          Class<?> generatedClass = BadFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Array fields are not supported, use java.nio.IntBuffer instead");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsArrayFieldOnStructRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Struct
        public record BadRecord(double[] data) {}
        """);
    var generatedUse = forSourceString("test.BadRecordUse", """
        package test;

        class BadRecordUse {
          Class<?> generatedClass = BadRecordFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Array fields are not supported, use java.nio.DoubleBuffer instead");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsBufferFieldOnStructRecord() {
    var source = forSourceString("test.BadRecord", """
        package test;
        @org.alveolo.ffm.Struct
        public record BadRecord(java.nio.IntBuffer data) {}
        """);
    var generatedUse = forSourceString("test.BadRecordUse", """
        package test;

        class BadRecordUse {
          Class<?> generatedClass = BadRecordFM.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "Buffer fields are not supported on records");
    assertThat(c).hadErrorCount(1);
  }
}
