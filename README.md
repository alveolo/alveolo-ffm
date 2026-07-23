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
- Specialized C variadic downcalls with `@FirstVariadicArg`.
- `@Struct` and `@Union` memory wrappers.
- Reusable native error-state capture with `@CallState`.
- `@DispatchTable` wrappers and `@Struct(vtable = true)` virtual calls.
- C name mapping with `@Symbol`.
- Native library lookup with `@Library`.
- Pass-by-value and pass-by-address control with `@Value` and `@Address`.
- Platform C scalars with `@CLong`, `@SizeT`, and `@WCharT`.
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
synthetic implementation details use `$f` only where they can share a scope
with user-named parameters.

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
  @SizeT long stringLength(String utf8z);
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

## Platform C Scalar Types

Ordinary Java primitives keep their fixed Java FFM layouts. Use a type-use
annotation when a declaration refers to a C scalar whose width depends on the
native ABI:

```java
@ForeignInterface
public interface LibC {
  MemorySegment l64a(@CLong long value);

  @SizeT long strlen(String utf8z);

  MemorySegment wmemchr(
      MemorySegment values, @WCharT int value, @SizeT long count);
}

@Struct
public record LDiv(@CLong long quot, @CLong long rem) {}
```

`@CLong` and `@SizeT` use Java `long`; `@WCharT` uses Java `int`. Generated
descriptors use the corresponding layout from
`Linker.nativeLinker().canonicalLayouts()`. For C `long` and `wchar_t`, the
generated class adapts a differing raw carrier once during class initialization
and the call site still uses `invokeExact` with the stable Java carrier.
Matching ABIs retain the raw downcall handle unchanged. `size_t` uses its native
layout directly with the 64-bit Java `long` carrier supported by current JDK
runtimes.

`@CLong` performs checked narrowing on an ABI with a 32-bit C `long`. `@SizeT`
preserves every Java `long` bit pattern. A 16-bit `wchar_t` accepts values from
`0` through `0xffff`; the annotation describes one scalar and does not choose a
wide-string encoding.

`@Address` remains a separate pass-mode annotation and composes with these
annotations. For example, `@Address @CLong long` passes a pointer to a native C
`long`, while the Java value remains a `long`.

Canonical scalar parameters, returns, and ordinary struct or union fields are
supported. Canonical scalar array and indexed-field elements are currently
rejected because a native element-width change requires explicit bulk
conversion rather than Java's fixed-width copy path. Generated field accessors
use the stable Java carrier. When adaptation is needed, `$get$F` and `$set$F`
method handles are derived once from the field's public raw `$VarHandle$F`.

## Variadic Native Functions

Use `@FirstVariadicArg(N)` to bind one concrete specialization of a C variadic
function. `N` is the zero-based index of the first variadic argument among the
method's declared native parameters:

```java
@ForeignInterface
public interface LibC {
  @FirstVariadicArg(2)
  int fcntl(int descriptor, int operation);

  @FirstVariadicArg(2)
  int fcntl(int descriptor, int operation, int argument);

  @FirstVariadicArg(2)
  int fcntl(
      int descriptor, int operation, MemorySegment argument);
}
```

The generated downcall for each method has a fixed descriptor and includes a
corresponding `Linker.Option.firstVariadicArg(...)`. A specialization with no
variadic values,
such as the two-argument `fcntl` above, must still carry the annotation because
the native variadic calling convention can differ on some platforms.

Declare variadic scalar parameters after C default argument promotion. Use
`int` instead of `boolean`, `byte`, `char`, or `short`, and use `double` instead
of `float`. Remove `@WCharT` and use plain `int` for a variadic wide-character
value because a narrow `wchar_t` is also promoted. The processor rejects the
unpromoted forms. Java-only
`SegmentAllocator` and `@CallState` parameters do not count toward `N`. If an
object symbol or virtual call inserts a native receiver before the declared
parameters, the processor passes `N + 1` to the linker.

## Captured Call State

Use `@CallState` for native thread-local state such as `errno`, Windows
`GetLastError`, or `WSAGetLastError`. The annotated interface declares one
zero-argument `int` accessor. Its generated implementation owns the reusable
capture segment:

```java
@CallState(
    value = "errno",
    overrides = @CallState.Override(
        os = Library.OS.WINDOWS,
        value = "GetLastError"))
public interface NativeErrorSpec {
  int error();

  default void throwIf(BooleanSupplier failure) {
    int error = error();
    if (failure.getAsBoolean()) {
      throw new IllegalStateException("native error: " + error);
    }
  }
}
```

A `@CallState` type is a Java-only foreign-method parameter. The processor
omits it from the native `FunctionDescriptor`, adds the corresponding
`Linker.Option.captureCallState(...)`, and supplies its segment in the position
required by the downcall handle:

```java
@ForeignInterface
public interface NativeApi {
  @Symbol("close")
  int closeRaw(NativeErrorSpec capture, int descriptor);

  default int close(NativeErrorSpec capture, int descriptor) {
    int result = closeRaw(capture, descriptor);
    capture.throwIf(() -> result == -1);
    return result;
  }
}

try (var arena = Arena.ofConfined()) {
  var capture = new NativeError(arena);
  NativeApiFFM.INSTANCE$F.close(capture, descriptor);
}
```

The failure condition stays in ordinary Java because it is specific to the
native API: it may inspect the return value, an out parameter, or any other
result. Capturing state does not itself imply that the call failed.

`Linker.Option.captureStateLayout()` is the platform-defined layout containing
all capturable state fields. The generated wrapper allocates that complete
layout because the linker requires it, but exposes and reads only the field
selected by `value` or the first matching platform override. Define separate
`@CallState` types for APIs with distinct error domains, such as
`GetLastError` and `WSAGetLastError`.

One call-state parameter is allowed per native method. A wrapper instance may
be reused sequentially, but every call overwrites its previous value. Inspect
it before the next call and do not share one instance between concurrent calls.

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

See [Efficient Arena Allocation](docs/ARENA-ALLOCATION.md) for the call-scoped
allocation strategy, including shared backing segments, aligned slicing, and
preallocated struct-return storage.

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

  void consumeVector(
      @Value @Sequence(4) float[] values);

  void consumeMatrix(
      @Value @Sequence(9) FloatBuffer values);
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
element count to equal `n`. By default the ABI type remains a pointer and the
annotation is an exact Java binding contract.

Add `@Value` to pass the fixed array or buffer contents as a native aggregate
instead. `@Sequence` is required in that case because the aggregate layout must
be known when the downcall handle is created. The generated descriptor uses a
synthetic one-field struct containing the sequence: the FFM linker accepts the
resulting group layout as a by-value argument while the Java API can remain an
array or buffer without a user-declared `@Struct` wrapper. `@Out` is rejected
for these arguments because a by-value argument cannot return mutations to the
caller.

C array parameters still decay to pointers. Use `@Value` only when the target
ABI actually defines a compatible aggregate-by-value parameter, such as an API
from another language or a C-compatible single-array struct ABI.

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
