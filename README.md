# alveolo-ffm ![Maven build](https://github.com/alveolo/alveolo-ffm/actions/workflows/maven.yml/badge.svg)

Alveolo FFM is a small annotation-processor layer over the Java Foreign
Function and Memory API. It lets you describe native functions, C structs,
unions, dispatch tables, buffers, and library loading in ordinary Java
declarations, then generates the repetitive `Linker`, `FunctionDescriptor`,
`MemoryLayout`, `VarHandle`, and copy glue for you.

The goal is not to hide FFM. The generated code still uses Java's native FFM
types where they matter. The goal is to keep binding code readable:

```java
@Library("affm_test")
@ForeignInterface
public interface NativeMath {
  int add_ints(int left, int right);

  @Symbol("scale_ints")
  void scale(
      @CountedBy("count") int[] values, int count, int factor);
}
```

The processor generates `NativeMathFFM`, with a ready-to-use singleton:

```java
var sum = NativeMathFFM.INSTANCE$F.add_ints(19, 23);

var values = new int[] {1, 2, 3};
NativeMathFFM.INSTANCE$F.scale(values, values.length, 10);
// values == {10, 20, 30}
```

## What It Generates

- `@ForeignInterface` bindings for native functions.
- `@Struct` and `@Union` memory wrappers.
- `@DispatchTable` wrappers and `@Struct(vtable = true)` virtual calls.
- C name mapping with `@Symbol`.
- Native library lookup with `@Library`.
- Pass-by-value and pass-by-address control with `@Value` and `@Address`.
- Primitive-array, record-array, and `java.nio.*Buffer` pointer parameters.
- Fixed, indexed inline arrays on memory-backed struct interfaces.
- Input/output transfer control with `@In` and `@Out`.
- Fixed and counted extents with `@Sequence` and `@CountedBy`.
- UTF-8 native string parameters.
- macOS CoreFoundation `CFStringRef` helpers.

Generated names are intentionally predictable:

- `NativeMath` -> `NativeMathFFM`
- `NativeMathSpec` -> `NativeMath`
- `timeval` -> `timevalFM`
- `Pair` -> `PairFM`

For annotated interfaces, a trailing `Spec` is removed instead of adding the
usual generated-class suffix. Records always retain their full declared name
and use the normal generated-class suffix. An explicitly configured generated
name still takes precedence. Generated vtable specifications follow the same
rule: a `NativeObjectSpec` struct produces `NativeObjectVtblSpec`, whose
generated dispatch-table class is `NativeObjectVtbl`.

Identifiers consumed by the processor must not end in the reserved `$F` or
`$f` suffix. Generated infrastructure and additional public helpers use `$F`;
synthetic implementation details use `$f`.

Annotated types must currently be top-level declarations; nested annotated
types are rejected with a compile-time diagnostic.

## Requirements

- Java 25
- Maven
- Native access enabled when running code that calls FFM

For unnamed-module applications and tests, pass:

```text
--enable-native-access=ALL-UNNAMED
```

For named modules, enable native access for the module that uses the generated
bindings.

## Maven Setup

Add the core artifact as a dependency and the processor as an annotation
processor:

```xml
<properties>
  <maven.compiler.release>25</maven.compiler.release>
</properties>

<dependencies>
  <dependency>
    <groupId>org.alveolo.ffm</groupId>
    <artifactId>alveolo-ffm-core</artifactId>
    <version>0.0.2-SNAPSHOT</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.13.0</version>
      <configuration>
        <annotationProcessorPaths>
          <path>
            <groupId>org.alveolo.ffm</groupId>
            <artifactId>alveolo-ffm-processor</artifactId>
            <version>0.0.2-SNAPSHOT</version>
          </path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

The processor jar also declares JPMS service providers. Maven's
`annotationProcessorPaths` setup is the usual choice for Maven projects; builds
that use javac's processor module path can discover the same processors through
`provides javax.annotation.processing.Processor`. Keep `alveolo-ffm-core` as a
normal dependency and keep `alveolo-ffm-processor` on the annotation processor
path rather than on the application runtime path.

The core module exports:

```java
module org.alveolo.ffm {
  exports org.alveolo.ffm;
  exports org.alveolo.ffm.macos;
}
```

Named-module applications should add `requires org.alveolo.ffm;` to their own
`module-info.java`.

## Native Functions

Declare an interface and mark it with `@ForeignInterface`:

```java
@ForeignInterface
public interface LibC {
  int abs(int number);

  @Symbol("strlen")
  long stringLength(String utf8z);
}
```

Generated code uses `Linker.nativeLinker()` and `downcallHandle(...)`.
Methods on the source interface become methods on the generated implementation:

```java
var length = LibCFFM.INSTANCE$F.stringLength("hello");
```

Default and static methods are ignored by the processor, so the interface can
still contain ordinary Java helpers. Native methods must currently be declared
directly on the annotated interface; inherited abstract methods are not scanned.

## Library Loading

Without `@Library`, generated bindings use the platform default lookup. Add
`@Library` when symbols live in a specific native library:

```java
@Library("affm_test")
@ForeignInterface
public interface AffmTest {
  int add_ints(int left, int right);
}
```

`@Library("name")` maps to the platform library file name:

- macOS: `libname.dylib`
- Linux: `libname.so`
- Windows: `name.dll`

For test-local or application-local native libraries, set:

```text
-Daffm.library.path=/path/to/native/libs
```

You can also load by path or, on macOS, by framework:

```java
@Library(value = "CoreFoundation", kind = Library.Kind.FRAMEWORK)
@ForeignInterface
public interface CoreFoundation {
  double CFAbsoluteTimeGetCurrent();
}
```

Multiple `@Library` annotations are allowed. The generated lookup combines the
matching libraries and falls back to the platform default lookup when no library
entry applies.

## Dispatch Tables

Use `@DispatchTable` for native tables of function pointers:

```java
@DispatchTable
public interface XyzVtbl {
  @Slot(0)
  int add(int left, int right);
}
```

The generated `XyzVtblFD` wrapper reads function pointers from address-sized
slots and exposes Java methods with the declared signatures.

For object-style native structs, `@Struct(vtable = true)` reserves the first
field for a dispatch table pointer. Methods annotated with `@Virtual` call
through the generated dispatch table wrapper; ordinary abstract methods still
describe fields, and methods with `@Symbol` call direct native symbols.

```java
@Struct(vtable = true, symbols = NativeApi.class)
public interface NativeObject {
  int field();

  NativeObject field(int value);

  @Virtual(2)
  int method(int arg);

  @Symbol("native_symbol")
  int call(int arg);
}
```

## Structs and Unions

Use `@Struct` for C structs. Records are convenient for value-style data:

```java
@Struct
public record div_t(int quot, int rem) {}
```

The processor generates a layout helper with:

- `MemoryLayout$F`
- `<field>$PathElement$F` path elements and `<field>$VarHandle$F` var handles
- `allocate$F(...)`
- `toMemorySegment$F(...)` and `fromMemorySegment$F(...)` record conversion
  helpers

For mutable memory-backed wrappers, use an interface:

```java
@Struct
public interface timeval {
  int tv_sec();
  int tv_usec();
}
```

Declare each scalar native field with a zero-argument getter only. Inline array
fields use the indexed getter form described below. Do not declare setter or
generated-helper overloads on the source interface; the processor rejects
those signatures. The generated `*FM` implementation adds fluent setters for
supported fields, which is why the following code can call `tv_sec(int)` and
`tv_usec(int)` even though they are not interface methods.

Generated usage:

```java
try (var arena = Arena.ofConfined()) {
  var tv = new timevalFM(arena)
      .tv_sec(1)
      .tv_usec(500);
}
```

Use `@Union` when fields share storage:

```java
@Union
public interface NumberBits {
  int i();
  float f();
}
```

Memory-backed struct interfaces describe fixed inline C arrays with indexed
getters. Put `@Sequence` on each `int` or `long` index to declare that native
dimension:

```java
@Struct
public interface Samples {
  int values(@Sequence(3) long index);
}
```

This maps to an inline C field such as `int values[3]`, not an `int*` pointer.
The generated wrapper implements `values(long)`, adds a fluent
`values(long, int)` setter, and exposes the complete field as
`valuesAsMemorySegment$F()`. A one-dimensional primitive field also provides
`valuesAsBuffer$F()`, a `valuesToArray$F()` snapshot, and an exact-length
`valuesFromArray$F(array)` replacement helper. Boolean and byte fields use
`ByteBuffer`; the boolean view exposes the underlying one-byte representation.
`valuesAsMemorySegment$F(index)` selects one element when byte-level access is
useful. The indexed path performs FFM bounds checks against the declared extent.

Use one index per C dimension. Parameter order is outermost to innermost, so
the final index is the fastest-varying one:

```java
@Struct
public interface Matrix {
  float cell(
      @Sequence(3) long row,
      @Sequence(4) long column);
}
```

Inline arrays may contain primitive values (including `boolean`), raw address
slots represented by `MemorySegment`, and annotated struct or union elements.
Value/address annotations apply to every element: `@Value Point`
produces contiguous inline `Point` storage, while `@Address Point` produces an
inline array of pointer slots. A value-style record element returned by an
indexed getter is a snapshot; a memory-backed interface element is a view that
aliases the parent segment.

Indexed array fields are supported on memory-backed interfaces. A source
interface declares only the indexed getter; generated setters and low-level
views must not be redeclared. Every dimension must be positive. Flexible array
members and pointer-to-array fields need an explicit `MemorySegment` binding;
they are not represented by `@Sequence(0)` or by Java multidimensional arrays.

Value-style record structs can instead use a one-dimensional array component:

```java
@Struct
public record Samples(@Sequence(3) int[] values) {}
```

Record array components are converted as snapshots rather than aliased views.
Their elements are limited to primitives or value-style `@Struct` records;
address elements and multidimensional Java arrays require a memory-backed
interface. The components remain ordinary mutable Java arrays, so the record's
generated `equals` and `hashCode` retain Java array reference semantics.

### Contiguous arrays of structs

Every generated struct or union companion also has helpers for explicit native
arrays:

```java
@Struct
record Point(int x, int y) {}

try (var arena = Arena.ofConfined()) {
  var points = PointFM.allocate$F(arena, 3);
  var first = PointFM.at$F(points, 0);
}
```

`allocate$F(allocator, count)` creates contiguous storage for `count`
layouts. `reinterpret$F(segment, count)` gives pointer-like native memory an
explicit array extent; the caller remains responsible for proving that many
elements are accessible and for keeping the underlying memory alive.

For a memory-backed interface, `at$F(segment, index)` returns an element view
that aliases the array. For a record, `at$F(...)` returns a detached snapshot.
No generic whole-element copy helper is generated.

## Value vs Address

The processor has defaults that match the common Java shape:

- records are value-like
- interfaces are address-like memory wrappers

Override that with `@Value` or `@Address` on a type use:

```java
@ForeignInterface
public interface NativePairs {
  int pair_sum(PairR value);

  int pair_ptr_sum(@Address PairR ref);

  @Value PairS make_pair(
      SegmentAllocator allocator, int left, int right);

  int pair_sum_interface(@Value PairS value);
}
```

A foreign-method parameter may also use the generated class of a memory-backed
interface directly. It is address-like by default, just like its source
interface; use `@Value` to override that pass mode:

```java
int tcgetattr(int fd, termiosFM value);
```

If a `@ForeignInterface` method returns a struct by value, the generated wrapper
needs caller-owned memory for the returned value. In that case the source method
must declare a `SegmentAllocator` parameter, as shown by `make_pair(...)`, and
the processor reports a compile error if it is missing. This requirement is
about returned values, not ordinary `@Address` arguments.

`@Address` on a primitive or record stores a native pointer instead of inline
data. For foreign-call arguments, the generated wrapper uses call-scoped
temporary storage when it has to materialize pointed-to data; the Java method
signature does not need an allocator for that. Explicit allocator-taking APIs
are generated on `*FM` conversion helpers and setters when a record struct
contains `@Address` components and the generated code must allocate pointed-to
native storage. Memory-backed interface structs reject scalar primitive-address
and record-address fields because an accessor-only wrapper has no hidden
allocator for the pointed-to memory. Indexed `@Address` fields are explicit
pointer-slot arrays and provide raw-address accessors; record pointee setters
take an allocator when materialization is required.

## Arrays and Buffers

Foreign-call pointer parameters may use one-dimensional primitive arrays,
value-style `@Struct` record arrays, and typed NIO buffers:

```java
@ForeignInterface
public interface Samples {
  void scale(int[] values);

  int sum(@In @Sequence(3) int[] values);

  void fill(@Out @Sequence(2) int[] values);

  void increment(ByteBuffer values);

  @Symbol("fill_two_ints")
  void fill(@Out @Sequence(2) IntBuffer values);

  void scalePrefix(
      @CountedBy("count") int[] values,
      int count,
      int factor);

  void offsetPairs(
      @CountedBy("count") Pair[] values,
      int count,
      int delta);
}
```

Supported array element types:

```text
boolean, byte, char, short, int, long, float, double
```

`char` means Java's 16-bit `char`. For C `char*` data, use `byte`/`ByteBuffer`
or `String` depending on the native API contract.

Supported buffer types:

```text
ByteBuffer, CharBuffer, ShortBuffer, IntBuffer,
LongBuffer, FloatBuffer, DoubleBuffer
```

Record-array elements are laid out contiguously using the record's generated
struct layout. Only value-style `@Struct` records are supported as call-array
elements; Java multidimensional arrays and arrays of memory-backed wrappers are
not native contiguous-array carriers.

Array and buffer return types are rejected because a native pointer return has
no implied extent or lifetime. Bind it as `MemorySegment`, then use the target
struct's `reinterpret$F(segment, count)` and `at$F(...)` helpers when the
native contract supplies a trustworthy element count.

### Extent

An unannotated array uses its full `array.length`. An unannotated buffer uses
the region from `position()` through `limit()`, without changing its position.

`@Sequence(n)` declares a fixed logical extent and requires the available
element count to equal `n`. On call parameters the ABI type is still a pointer;
the annotation is an exact Java binding contract, not C array-by-value
semantics.

Use `@CountedBy("count")` when a sibling integral parameter carries the active
element count:

- the named count parameter must be a `byte`, `short`, `int`, or `long`
- the count is an element count, not a byte count
- the generated wrapper requires `0 <= count <= available elements`
- only the prefix `[0, count)` is transferred, or exposed directly for a
  direct buffer
- the count remains an ordinary explicit argument in the native ABI

`@Sequence` and `@CountedBy` cannot be combined on one parameter. Use
`@Sequence` for an exact fixed extent, `@CountedBy` for a runtime prefix, and
neither for the complete Java carrier.

### Copies and lifetime

Transfer direction controls copying:

- unannotated arrays and heap buffers are copied in before the native call and
  copied out after the call
- `@In` copies in only
- `@Out` copies out only
- direct buffers are passed directly with `MemorySegment.ofBuffer(...)`; a
  counted direct buffer passes a slice covering the selected prefix

Buffers must be writable whenever copy-out is enabled. Direct typed buffers
other than `ByteBuffer` must also use native byte order, because their storage
is passed without element conversion. Heap typed buffers are copied as logical
elements and do not have that direct-storage restriction.

Copy-out for record arrays replaces the transferred entries with fresh record
snapshots; entries outside a `@CountedBy` prefix remain untouched. With `@Out`,
record-array entries in the transferred prefix may initially be `null` because
the wrapper does not read them before the call.

`@In` and `@Out` describe wrapper copies, not native `const` or memory
protection. They do not change direct-buffer behavior: native code receives the
buffer's storage and may read or write it regardless of the annotation. A
counted direct buffer likewise remains zero-copy. Its Java segment view is
bounded to the selected prefix, but native pointer arithmetic is outside Java's
bounds checks, so native code must still honor the explicit count.

Temporary native copies live only for the duration of the call. Native code
must not retain their addresses. Direct buffers are kept reachable for the
call, but the generated wrapper does not manage a pointer retained afterward;
use an explicit, suitably scoped `MemorySegment` API for persistent native
pointers.

Inline array views such as `valuesAsMemorySegment$F()` alias their containing
struct and inherit its arena lifetime. Pointer elements stored with `@Address`
do not transfer ownership: the containing struct does not extend the pointee
lifetime. Record pointee setters may materialize storage in a caller-supplied
allocator, whose lifetime the caller must manage.

## CoreFoundation Strings

On macOS, `org.alveolo.ffm.macos.CFString` can convert Java strings to
`CFStringRef` parameters:

```java
@Library(value = "CoreFoundation", kind = Library.Kind.FRAMEWORK)
@ForeignInterface
public interface CoreStrings {
  long CFStringGetLength(@CFString String value);

  @Symbol("CFStringCreateWithCString")
  @CFString(owned = true)
  String create(MemorySegment allocator, String cString, int encoding);
}
```

Parameter `@CFString String` values are converted before the call and released
afterwards. Return values are borrowed by default; set `owned = true` for
CoreFoundation Create/Copy-rule returns that the generated wrapper must release.

## Development

Build everything:

```sh
mvn clean install
```

Run the processor-focused tests:

```sh
mvn -pl processor -am test
```

The benchmark module contains end-to-end native tests. They compile a small C
library during the JUnit setup and exercise generated bindings against the real
FFM runtime.

Build the benchmark module and its dependencies:

```sh
mvn -pl benchmark -am package -DskipTests
```

## Status

This project targets Java 25 and is intentionally small. The generated code is
plain Java source, so when something looks surprising, inspect the generated
`*FFM` and `*FM` classes first. The library is most useful when you want FFM's
native performance and ownership model, but you do not want every binding file
to hand-write the same boilerplate.
