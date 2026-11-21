package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ForeignMemoryProcessorTest extends AbstractProcessorTest {
  @ParameterizedTest
  @ValueSource(strings = {
      "@Struct record Recursive(Recursive child) {}",
      "@Struct record A(B child) {} @Struct record B(A child) {}",
      "@Struct record Recursive(@Sequence(2) Recursive[] children) {}",
      """
      @Struct interface A { @Value B child(); }
      @Union interface B { @Value AF parent(@Sequence(2) int index); }
      """,
      """
      @Struct interface Base { @Value Derived child(); }
      @Struct interface Derived extends Base {}
      """
  })
  void rejectsRecursiveInlineLayouts(String declarations) {
    var source = forSourceString("test.RecursiveLayouts", """
        package test;
        import org.alveolo.ffm.*;
        %s
        """.formatted(declarations));

    var c = compile(source);

    assertThat(c).hadErrorCount(1);
    assertThat(c).hadErrorContaining(
        "Recursive inline layout: a struct or union cannot contain itself"
            + " by value").inFile(source);
  }

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
          SnapshotSpecF snapshot;
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
          SpecF value;
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
          NativeObjectV vtable;
        }
        """);

    var c = compile(source);

    assertThat(c).succeeded();
    assertThat(c).generatedSourceFile("test.NativeObjectVSpec");
  }

  @Test
  void explicitStructNameOwnsVtableNames() {
    var c = compile("memory/object/ExplicitVtable.java");

    assertThat(c).succeeded();
    assertGenerated(c, "test.Other", "memory/object/Other.java");
    assertGenerated(c, "test.OtherV", "memory/object/OtherV.java");
    assertGenerated(c, "test.OtherVF", "memory/object/OtherVF.java");
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
  @Disabled
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
    assertGenerated(c, "pkg.timevalF", "memory/struct/timevalF.java");
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
        "pkg.StructAF", "memory/struct/multiple/StructAF.java");
    assertGenerated(c,
        "pkg.StructBF", "memory/struct/multiple/StructBF.java");
  }

  @Test
  void generatesUnionClass() {
    var c = compile("memory/union/Union.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.UnionF", "memory/union/UnionF.java");
  }

  @Test
  void generatesNestedStruct() {
    var c = compile("memory/nested/Inner.java", "memory/nested/Outer.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.InnerF", "memory/nested/InnerF.java");
    assertGenerated(c, "pkg.OuterF", "memory/nested/OuterF.java");
  }

  @Test
  void generatesInheritedStructFields() {
    var c = compile("memory/inheritance/Derived.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.BaseF", "memory/inheritance/BaseF.java");
    assertGenerated(c, "pkg.DerivedF",
        "memory/inheritance/DerivedF.java");
  }

  @Test
  void overridesInheritedIndexedFluentSetters() {
    var c = compile("memory/inheritance/IndexedDerived.java");

    assertThat(c).succeeded();
    assertGenerated(c, "pkg.IndexedDerivedF",
        "memory/inheritance/IndexedDerivedF.java");
  }

  @Test
  void generatesFieldsInheritedWithoutSource() {
    var source = forSourceString("test.BinaryDerived", """
        package test;

        @org.alveolo.ffm.Struct
        public interface BinaryDerived extends
            org.alveolo.ffm.processor.fixture.BinaryFieldsParent {
          byte own();
        }
        """);

    var c = compile(source);

    assertThat(c).succeeded();
    assertGenerated(c, "test.BinaryDerivedF",
        "memory/inheritance/BinaryDerivedF.java");
  }

  @Test
  void failsFieldsOnStruct() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Fields("value")
        @org.alveolo.ffm.Struct
        interface Bad {
          int value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Fields is not allowed on @Struct or @Union interfaces");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsUnmappedInheritedStructMethod() {
    var source = forSourceString("test.Bad", """
        package test;
        interface Parent {
          int value();
        }
        @org.alveolo.ffm.Struct
        interface Bad extends Parent {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Inherited abstract method must be mapped by @Fields or "
            + "overridden directly on @Struct");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsRedefinedFieldMapping() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Fields("value")
        interface Parent {
          int value();
        }
        @org.alveolo.ffm.Fields("value")
        interface Child extends Parent {}
        @org.alveolo.ffm.Struct
        interface Bad extends Child {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Field mapping is already defined: value");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsChangingAnInheritedFieldToVirtual() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Fields("value")
        interface Parent {
          int value();
        }
        @org.alveolo.ffm.Struct(vtable = true)
        interface Bad extends Parent {
          @Override
          @org.alveolo.ffm.Virtual(0)
          int value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Method mapping is already defined by @Fields or @Struct");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsRedefinedVirtualMapping() {
    var source = forSourceString("test.Bad", """
        package test;
        interface Parent {
          @org.alveolo.ffm.Virtual(0)
          int call();
        }
        @org.alveolo.ffm.Struct(vtable = true)
        interface Bad extends Parent {
          @Override
          @org.alveolo.ffm.Virtual(1)
          int call();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "Method mapping is already defined by @Virtual");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsChangingTheReturnTypeOfAnInheritedMapping() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Fields("value")
        interface Parent {
          Number value();
        }
        @org.alveolo.ffm.Struct
        interface Bad extends Parent {
          @Override
          Integer value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "A mapped method override must preserve its return type");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void failsMultipleStructBases() {
    var source = forSourceString("test.Bad", """
        package test;
        @org.alveolo.ffm.Struct interface Left { int left(); }
        @org.alveolo.ffm.Struct interface Right { int right(); }
        @org.alveolo.ffm.Struct interface Bad extends Left, Right {}
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "A @Struct may inherit from only one @Struct base");
    assertThat(c).hadErrorCount(1);
  }

  @Test
  void generatesIndexedArrayFieldsAndRecordSnapshots() {
    var use = forSourceString("pkg.ArrayFieldsUse", """
        package pkg;

        import java.lang.foreign.*;
        import java.nio.ByteBuffer;

        final class ArrayFieldsUse {
          static void exercise(Arena arena) {
            var fields = new ArrayFieldsF(arena);
            var union = new ArrayUnionF(arena);
            var interfaceElement = new ArrayCellF(arena).value(9);
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
            var snapshotMemory = ArraySnapshotF.toMemorySegment$F(
                arena, snapshot);
            ArraySnapshot detached = ArraySnapshotF.fromMemorySegment$F(
                snapshotMemory);

            var pointArray = ArrayPointF.allocateSequence$F(arena, 2);
            ArrayPoint first = ArrayPointF.at$F(pointArray, 0);
            MemorySegment bounded = ArrayPointF.reinterpretSequence$F(
                pointArray, 2);
          }
        }
        """);
    var c = compile(forTestResource("memory/array/ArrayFields.java"), use);
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ArrayFieldsF", "memory/array/ArrayFieldsF.java");
    assertGenerated(c, "pkg.ArraySnapshotF",
        "memory/array/ArraySnapshotF.java");
    assertGenerated(c, "pkg.ArrayUnionF", "memory/array/ArrayUnionF.java");
    assertGenerated(c, "pkg.AllocatingArraySnapshotF",
        "memory/array/AllocatingArraySnapshotF.java");
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
            new NamedIndicesF(arena)
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
          Class<?> generatedClass = BufferStructF.class;
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
          Class<?> generatedClass = BadStructF.class;
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
    assertGenerated(c, "pkg.PairBoxF", "memory/address/PairBoxF.java");
    assertGenerated(c, "pkg.IntBoxF", "memory/address/IntBoxF.java");
    assertGenerated(c, "pkg.OuterF", "memory/address/OuterF.java");
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
          Class<?> generatedClass = BadStructF.class;
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
        @org.alveolo.ffm.Struct(name = "other.BadStructF")
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
        @org.alveolo.ffm.Union(name = "other.BadUnionF")
        public interface BadUnion {
          int value();
        }
        """);

    var c = compile(source);

    assertThat(c).hadErrorContaining(
        "@Union name must be a simple Java class name");
  }

  @Test
  void placesVirtualReturnAllocatorBeforeReceiver() {
    var c = compile("memory/object/VirtualPairs.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.VirtualPairsV",
        "memory/object/VirtualPairsV.java");
  }

  @Test
  void generatesVirtualArrayBridgesWithDefaultConvenienceMethods() {
    var c = compile("memory/object/ArrayVirtual.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ArrayVirtualV",
        "memory/object/ArrayVirtualV.java");
  }

  @Test
  void generatesVtableHierarchyWithoutAllocationHelpers() {
    var c = compile("memory/object/VtableHierarchy.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.VtableBaseF", "memory/object/VtableBaseF.java");
    assertGenerated(c, "pkg.VtableMidF", "memory/object/VtableMidF.java");
    assertGenerated(c, "pkg.VtableLeafF", "memory/object/VtableLeafF.java");
    assertGenerated(c, "pkg.VtableMidV", "memory/object/VtableMidV.java");
    assertGenerated(c, "pkg.VtableLeafV", "memory/object/VtableLeafV.java");
  }

  @ParameterizedTest
  @ValueSource(strings = {"VtableBaseF", "VtableMidF", "VtableLeafF"})
  void rejectsVtableAllocation(String wrapper) {
    var use = forSourceString("pkg.Allocation", """
        package pkg;
        class Allocation {
          void allocate(java.lang.foreign.Arena arena) {
            new %1$s(arena);
            %1$s.allocate$F(arena);
            %1$s.allocateSequence$F(arena, 2);
          }
        }
        """.formatted(wrapper));

    var c = compile(forTestResource("memory/object/VtableHierarchy.java"), use);

    assertThat(c).failed();
    assertThat(c).hadErrorCount(3);
  }

  @Test
  void generatesObjectVtblStruct() {
    var c = compile(
        "memory/object/NativeApi.java",
        "memory/object/Obj.java",
        "memory/object/VirtualObj.java");
    assertThat(c).succeeded();
    assertGenerated(c, "pkg.ObjF", "memory/object/ObjF.java");
    assertGenerated(c, "pkg.VirtualObjF",
        "memory/object/VirtualObjF.java");
    assertGenerated(c, "pkg.VirtualObjV",
        "memory/object/VirtualObjV.java");
    assertGenerated(c, "pkg.VirtualObjVF",
        "memory/object/VirtualObjVF.java");
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

  @ParameterizedTest
  @ValueSource(strings = {
      "interface Both { int value(); }", "record Both(int value) {}"
  })
  void rejectsStructAndUnionOnSameType(String declaration) {
    var source = forSourceString("test.Both", """
        package test;
        @org.alveolo.ffm.Struct
        @org.alveolo.ffm.Union
        %s
        """.formatted(declaration));

    var c = compile(source);

    assertThat(c).hadErrorCount(1);
    assertThat(c).hadErrorContaining(
        "@Struct and @Union cannot be used on the same type").inFile(source);
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadRecordF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadF.class;
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
          Class<?> generatedClass = BadRecordF.class;
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
          Class<?> generatedClass = BadRecordF.class;
        }
        """);

    var c = compile(source, generatedUse);

    assertThat(c).hadErrorContaining(
        "NIO Buffer types are not supported as record components");
    assertThat(c).hadErrorCount(1);
  }
}
