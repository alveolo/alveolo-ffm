package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @Test
  void stripsSpecFromInterfaceNamesButNotRecordNames() {
    var source = forSourceString("test.SpecNames", """
        package test;

        @org.alveolo.ffm.Struct
        interface PointSpec {
          int x();
        }

        @org.alveolo.ffm.Union
        interface ValueSpec {
          int i();
        }

        @org.alveolo.ffm.Struct
        record SnapshotSpec(int value) {}

        class SpecNames {
          Point point;
          Value value;
          SnapshotSpecFM snapshot;
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void explicitNameWinsOverSpecConvention() {
    var source = forSourceString("test.RenamedSpec", """
        package test;

        @org.alveolo.ffm.Struct(name = "ExactGeneratedName")
        interface RenamedSpec {
          int value();
        }

        class UseRenamedSpec {
          ExactGeneratedName value;
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void doesNotStripANameThatIsOnlySpec() {
    var source = forSourceString("test.Spec", """
        package test;

        @org.alveolo.ffm.Struct
        interface Spec {
          int value();
        }

        class UseSpec {
          SpecFM value;
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void specVtableUsesPlainGeneratedClassNames() {
    var source = forSourceString("test.NativeObjectSpec", """
        package test;

        @org.alveolo.ffm.Struct(vtable = true)
        interface NativeObjectSpec {
          @org.alveolo.ffm.Virtual(0)
          int call();
        }

        class UseNativeObject {
          NativeObject object;
          NativeObjectVtbl vtable;
        }
        """);

    var c = compile(source);

    assertThat(c).succeeded();
    assertThat(c).generatedSourceFile("test.NativeObjectVtblSpec");
  }

  @Test
  void rejectsReservedMethodAndRecordComponentSuffixes() {
    var method = forSourceString("test.BadMethod", """
        package test;

        @org.alveolo.ffm.Struct
        interface BadMethod {
          int value$F();
        }
        """);
    var component = forSourceString("test.BadRecord", """
        package test;

        @org.alveolo.ffm.Struct
        record BadRecord(int value$f) {}
        """);

    var c = compile(method, component);

    assertThat(c).hadErrorContaining(
        "User method names ending in '$F' or '$f' are reserved");
    assertThat(c).hadErrorContaining(
        "User record component names ending in '$F' or '$f' are reserved");
    assertThat(c).hadErrorCount(2);
  }

  @Test
  void rejectsGeneratedClassNamesContainingDollar() {
    var override = forSourceString("test.BadName", """
        package test;

        @org.alveolo.ffm.Struct(name = "Bad$Generated")
        interface BadName {
          int value();
        }
        """);
    var defaultName = forSourceString("test.Bad$Source", """
        package test;

        @org.alveolo.ffm.Struct
        interface Bad$Source {
          int value();
        }
        """);

    var c = compile(override, defaultName);

    assertThat(c).hadErrorContaining(
        "Generated @Struct class name must not contain '$'");
    assertThat(c).hadErrorCount(2);
  }

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
  void printLinkerCaptureStateNames() {
    var capturedNames = Linker.Option.captureStateLayout()
        .memberLayouts().stream()
        .map(MemoryLayout::name)
        .flatMap(Optional::stream)
        .toList();
    IO.println("Capture names: " + capturedNames);
  }

  @Test
  void generatesStructClass() {
    var c = compile("memory/struct/timeval.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.timevalFM", "memory/struct/timevalFM.java");
  }

  @Test
  void generatedSourcesDoNotDependOnImportedTypeNames() {
    var source = forSourceString("test.ShadowedNames", """
        package test;

        final class MemorySegment {}
        final class MemoryLayout {}
        final class ValueLayout {}
        final class SegmentAllocator {}
        final class Arena {}
        final class Linker {}
        final class SymbolLookup {}
        final class FunctionDescriptor {}
        final class MethodHandle {}

        @org.alveolo.ffm.Struct
        interface ShadowedStruct {
          int value();
        }

        @org.alveolo.ffm.ForeignInterface
        interface ShadowedFunctions {}

        @org.alveolo.ffm.DispatchTable
        interface ShadowedDispatch {
          @org.alveolo.ffm.Slot(0)
          int call(int value);
        }
        """);

    assertThat(compile(source)).succeeded();
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
  void generatesIndexedArrayFieldsAndRecordSnapshots() {
    var use = forSourceString("pkg.ArrayFieldsUse", """
        package pkg;

        import java.lang.foreign.*;
        import java.nio.ByteBuffer;

        final class ArrayFieldsUse {
          static void exercise(Arena arena) {
            var fields = new ArrayFieldsFM(arena);
            var union = new ArrayUnionFM(arena);
            var interfaceElement = new ArrayCellFM(arena).value(9);
            union.words(3, (short) 9);
            fields.flags(0, true)
                .matrix(1, 2, 7)
                .points(0, new ArrayPoint(1, 2))
                .pointers(arena, 0, new ArrayPoint(3, 4))
                .cells(0, interfaceElement)
                .references(0, interfaceElement)
                .raw(0, MemorySegment.NULL);
            boolean flag = fields.flags(0);
            int cell = fields.matrix(1, 2);
            ArrayPoint point = fields.points(0);
            ArrayPoint nullable = fields.pointers(0);
            ArrayCell inlineCell = fields.cells(0);
            ArrayCell referencedCell = fields.references(0);
            MemorySegment raw = fields.rawAsAddress$F(0);
            MemorySegment whole = fields.matrixAsMemorySegment$F();
            MemorySegment leaf = fields.matrixAsMemorySegment$F(1, 2);
            ByteBuffer buffer = fields.flagsAsBuffer$F();
            boolean[] copy = fields.flagsToArray$F();
            fields.flagsFromArray$F(new boolean[] {true, false, true});

            var snapshot = new ArraySnapshot(
                new byte[] {1, 2, 3, 4},
                new ArrayPoint[] {
                    new ArrayPoint(5, 6), new ArrayPoint(7, 8)});
            var snapshotMemory = ArraySnapshotFM.toMemorySegment$F(
                arena, snapshot);
            ArraySnapshot detached = ArraySnapshotFM.fromMemorySegment$F(
                snapshotMemory);

            var pointArray = ArrayPointFM.allocate$F(arena, 2);
            ArrayPoint first = ArrayPointFM.at$F(pointArray, 0);
            MemorySegment bounded = ArrayPointFM.reinterpret$F(
                pointArray, 2);
          }
        }
        """);
    var c = compile(forTestResource("memory/array/ArrayFields.java"), use);
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ArrayFieldsFM", "memory/array/ArrayFieldsFM.java");
    assertGenerated(c, "pkg.ArraySnapshotFM",
        "memory/array/ArraySnapshotFM.java");
    assertGenerated(c, "pkg.ArrayUnionFM", "memory/array/ArrayUnionFM.java");
    assertGenerated(c, "pkg.AllocatingArraySnapshotFM",
        "memory/array/AllocatingArraySnapshotFM.java");
  }

  @Test
  void avoidsIndexedSetterParameterNameCollisions() {
    var fields = forSourceString("test.NamedIndices", """
        package test;

        @org.alveolo.ffm.Struct
        record Point(int x, int y) {}

        @org.alveolo.ffm.Struct
        interface NamedIndices {
          int values(@org.alveolo.ffm.Sequence(2) long value);

          @org.alveolo.ffm.Address
          Point pointers(
              @org.alveolo.ffm.Sequence(2) long allocator);

          @org.alveolo.ffm.Address
          Point pointerValues(
              @org.alveolo.ffm.Sequence(2) long value);

          @org.alveolo.ffm.Address
          Point pointerAddresses(
              @org.alveolo.ffm.Sequence(2) long address);

          int packageNames(
              @org.alveolo.ffm.Sequence(2) long java);

          @org.alveolo.ffm.Address
          Point pointerPackages(
              @org.alveolo.ffm.Sequence(2) long java);
        }
        """);
    var use = forSourceString("test.NamedIndicesUse", """
        package test;

        final class NamedIndicesUse {
          static void exercise(java.lang.foreign.Arena arena) {
            new NamedIndicesFM(arena)
                .values(0, 1)
                .pointers(arena, 0, new Point(2, 3));
          }
        }
        """);
    var packageRoot = forSourceString("index0.PackageRoot", """
        package index0;

        @org.alveolo.ffm.Struct
        record Point(int x) {}

        @org.alveolo.ffm.Struct
        interface PackageRoot {
          Point points(
              @org.alveolo.ffm.Sequence(2) long ignored);
        }
        """);

    var c = compile(fields, use, packageRoot);

    assertThat(c).succeeded();
  }

  @Test
  void indexesSyntheticDimensionNamesWithoutFieldCollisions() {
    var source = forSourceString("test.IndexedNames", """
        package test;

        @org.alveolo.ffm.Struct
        interface IndexedNames {
          int a(@org.alveolo.ffm.Sequence(2) long index);
          int a$0();
        }
        """);

    assertThat(compile(source)).succeeded();
  }

  @Test
  void rejectsBufferStructField() {
    var source = forSourceString("pkg.BufferStruct", """
        package pkg;

        @org.alveolo.ffm.Struct
        public interface BufferStruct {
          @org.alveolo.ffm.Sequence(3)
          java.nio.IntBuffer data();
        }
        """);
    var generatedUse = forSourceString("pkg.BufferStructUse", """
        package pkg;

        class BufferStructUse {
          Class<?> generatedClass = BufferStructFM.class;
        }
        """);
    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "NIO Buffer types are not supported as @Struct or @Union fields; "
            + "declare an indexed element accessor");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsNonPositiveIndexedDimension() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        interface Bad {
          int value(@org.alveolo.ffm.Sequence(0) long index);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Indexed field @Sequence value must be positive");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsNonIntegralIndexedDimension() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        interface Bad {
          int value(@org.alveolo.ffm.Sequence(2) String index);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Indexed field parameters must be int or long");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnsupportedIndexedElement() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        interface Bad {
          String value(@org.alveolo.ffm.Sequence(2) long index);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Indexed fields support primitives, MemorySegment, and @Struct or "
            + "@Union elements");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsMultidimensionalRecordArray() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        record Bad(@org.alveolo.ffm.Sequence(2) int[][] value) {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Multidimensional Java array record fields are not supported");
  }

  @Test
  void failsIndexedFieldAnnotatedVirtual() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        interface Bad {
          @org.alveolo.ffm.Virtual(0)
          int values(@org.alveolo.ffm.Sequence(2) long index);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Indexed field declarations cannot be annotated @Virtual or @Symbol");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsIndexedFieldAnnotatedSymbol() {
    var source = forSourceString("test.Bad", """
        package test;

        @org.alveolo.ffm.ForeignInterface
        interface NativeApi {}

        @org.alveolo.ffm.Struct(symbols = NativeApi.class)
        interface Bad {
          @org.alveolo.ffm.Symbol("values")
          int values(@org.alveolo.ffm.Sequence(2) long index);
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Indexed field declarations cannot be annotated @Virtual or @Symbol");
    assertThat(c).hadErrorCount(1);
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
  void failsNestedStruct() {
    var source = forSourceString("test.Outer", """
        package test;
        class Outer {
          @org.alveolo.ffm.Struct
          interface Nested {
            int value();
          }
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Nested @Struct types are not yet supported");
    assertThat(c).hadErrorCount(1);
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
  void failsNestedUnion() {
    var source = forSourceString("test.Outer", """
        package test;
        class Outer {
          @org.alveolo.ffm.Union
          interface Nested {
            int value();
          }
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Nested @Union types are not yet supported");
    assertThat(c).hadErrorCount(1);
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
  void allowsFormerVtableMemberName() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Bad {
          int ff$vtbl();
        }
        """);

    var c = compile(source);

    assertThat(c).succeeded();
  }

  @Test
  void allowsPropertyNamedLikeVtableMetadataPrefix() {
    var source = forSourceString("test.Valid", """
        package test;
        @org.alveolo.ffm.Struct(vtable = true)
        public interface Valid {
          int Vtable();
        }
        """);

    var c = compile(source);

    assertThat(c).succeeded();
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
  void failsIndexedFieldWithoutSequence() {
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

    assertThat(c).hadErrorContaining(
        "Each indexed field parameter must carry @Sequence");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsStructSetterDeclaration() {
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
  void failsFluentStructSetterDeclaration() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          int x();
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

    assertThat(c).hadErrorContaining(
        "Unsupported accessor signature for field 'x'");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsNestedRecordSetterDeclaration() {
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
  void failsBufferHelperDeclaration() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          @org.alveolo.ffm.Sequence(3)
          java.nio.IntBuffer data();
          int data(int index);
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
        "NIO Buffer types are not supported as @Struct or @Union fields; "
            + "declare an indexed element accessor");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsSequenceOnScalarStructField() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct
        public interface Bad {
          @org.alveolo.ffm.Sequence(2)
          int value();
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
        "@Sequence on interface fields belongs on int or long indexed "
            + "accessor parameters");
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
        "Array-returning interface fields are not supported; declare an "
            + "indexed element accessor");
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
        "Record array fields must carry one positive @Sequence");
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
        "NIO Buffer types are not supported as record components");
    assertThat(c).hadErrorCount(1);
  }
}
