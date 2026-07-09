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
  void scale(int[] values, int count, int factor);
}
```

The processor generates `NativeMathFFM`, with a ready-to-use singleton:

```java
var sum = NativeMathFFM.INSTANCE.add_ints(19, 23);

var values = new int[] {1, 2, 3};
NativeMathFFM.INSTANCE.scale(values, values.length, 10);
// values == {10, 20, 30}
```

## What It Generates

- `@ForeignInterface` bindings for native functions.
- `@Struct` and `@Union` memory wrappers.
- `@DispatchTable` wrappers and `@Struct(vtable = true)` virtual calls.
- C name mapping with `@Symbol`.
- Native library lookup with `@Library`.
- Pass-by-value and pass-by-address control with `@Value` and `@Address`.
- Java array and `java.nio.*Buffer` pointer parameters.
- Fixed-size `java.nio.*Buffer` fields on memory-backed struct interfaces.
- Input/output transfer control with `@In`, `@Out`, and `@Sequence`.
- UTF-8 native string parameters.
- macOS CoreFoundation `CFStringRef` helpers.

Generated names are intentionally predictable:

- `NativeMath` -> `NativeMathFFM`
- `timeval` -> `timevalFM`
- `Pair` -> `PairFM`

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
var length = LibCFFM.INSTANCE.stringLength("hello");
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

- `FM$LAYOUT`
- path elements and var handles
- `allocate(...)`
- record conversion helpers

For mutable memory-backed wrappers, use an interface:

```java
@Struct
public interface timeval {
  int tv_sec();
  int tv_usec();
}
```

Declare each native field with a zero-argument getter only. Do not declare
setter or generated-helper overloads on the source interface; the processor
rejects those signatures. The generated `*FM` implementation adds fluent
setters for supported fields, which is why the following code can call
`tv_sec(int)` and `tv_usec(int)` even though they are not interface methods.

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

Memory-backed struct interfaces can also expose fixed-size NIO buffer fields:

```java
@Struct
public interface Samples {
  @Sequence(3)
  IntBuffer values();
}
```

Here `@Sequence(3)` is part of the native field layout. The generated layout
uses a sequence of three elements and exposes `values()`, `values$MemorySegment()`,
indexed get/set methods, and an array replacement helper. Buffer fields are
supported on memory-backed interfaces only; records should use primitive or
nested annotated fields instead.

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
native storage. Memory-backed interface structs reject primitive address fields
and record address fields because an accessor-only wrapper has no hidden
allocator for the pointed-to memory.

## Arrays and Buffers

Primitive arrays and typed NIO buffers can be used as pointer parameters:

```java
@ForeignInterface
public interface Samples {
  void scale(int[] values);

  int sum(@In @Sequence(3) int[] values);

  void fill(@Out @Sequence(2) int[] values);

  void increment(ByteBuffer values);

  @Symbol("fill_two_ints")
  void fill(@Out @Sequence(2) IntBuffer values);
}
```

Supported array element types:

```text
byte, char, short, int, long, float, double
```

`char` means Java's 16-bit `char`. For C `char*` data, use `byte`/`ByteBuffer`
or `String` depending on the native API contract.

Supported buffer types:

```text
ByteBuffer, CharBuffer, ShortBuffer, IntBuffer,
LongBuffer, FloatBuffer, DoubleBuffer
```

Transfer rules:

- unannotated arrays and heap buffers are copied in before the native call and
  copied out after the call
- `@In` copies in only
- `@Out` copies out only
- direct buffers are passed directly with `MemorySegment.ofBuffer(...)`
- `@In` and `@Out` do not change direct-buffer behavior, because no copy is
  performed

`@Sequence(n)` validates the Java argument size:

- arrays use `array.length`
- buffers use `buffer.remaining()`

Without `@Sequence`, the generated temporary native segment uses the passed
array length or buffer remaining size.

On struct interface buffer fields, `@Sequence(n)` defines the fixed native field
size instead of validating an argument. Java array fields are not generated as
struct fields; use a typed NIO buffer field for fixed-size inline storage.

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
