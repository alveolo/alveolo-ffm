# Native Globals

Status: design decision, 2026-07-18.

## Decision

Do not add a dedicated `@Global` API yet. Native data symbols can already be
bound with the generated `SymbolLookup$F` and the memory layouts and accessors
generated for `@Struct` and `@Union` types. This composition covers scalar
cells, pointer cells, inline aggregates, and fixed arrays while leaving
library-specific lifetime and synchronization rules visible.

This decision adds no annotations, methods, generated members, or runtime
behavior. It records the supported recipes and the conditions under which a
first-class globals API should be reconsidered.

## FFM Model

A native symbol names storage, not necessarily the value stored there. Java
25 [`SymbolLookup`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/SymbolLookup.html)
returns the symbol address as a zero-length `MemorySegment`. To read or write a
global, assign that address bounds matching the native declaration before
accessing it:

```java
var storage = NativeApiFFM.SymbolLookup$F
    .findOrThrow("counter")
    .reinterpret(ValueLayout.JAVA_INT.byteSize());

int value = storage.get(ValueLayout.JAVA_INT, 0L);
storage.set(ValueLayout.JAVA_INT, 0L, value + 1);
```

The examples below assume these imports and a foreign interface whose
generated implementation owns the correct library lookup:

```java
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Union;

@Struct
interface IntCell {
  int value();
}

@Struct
interface AddressCell {
  MemorySegment value();
}
```

`IntCellFM` and `AddressCellFM` are ordinary generated wrappers. Their memory
layouts are respectively one native Java `int` and one native address.

## Binding Recipes

### Scalar cells

For declarations such as `extern int counter`, wrap the symbol address itself:

```java
var counter = IntCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("counter"));

int current = counter.value();
counter.value(current + 1);
```

The generated fluent setter is available even though the source `@Struct`
interface declares only its getter.

For native data that must not be changed through this binding, make the symbol
segment read-only before wrapping it:

```java
var version = IntCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F
        .findOrThrow("library_version_number")
        .asReadOnly());

int current = version.value();
```

Any generated setter called through `version` then fails with
`IllegalArgumentException`. This protects against accidental Java writes; it
does not discover native `const`, change the underlying page protection, or
prevent native code from changing the storage.

### Pointer-valued globals

For declarations such as `extern FILE *stdout`, `extern char **environ`, or
`extern Object *constant`, the symbol identifies a cell containing an address.
The cell must be wrapped first, and its value then read:

```java
var standardOutputCell = AddressCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("stdout"));

MemorySegment standardOutput = standardOutputCell.value();
```

The identical cell shape reads `environ` or an exported object constant:

```java
MemorySegment environment = AddressCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("environ"))
    .value();

MemorySegment booleanTrue = AddressCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("kCFBooleanTrue"))
    .value();
```

The returned segment represents the pointer value and normally has zero
length. It can be passed directly to a native function. Dereference it only
after assigning bounds justified by the target type or by a library-provided
size. A null pointer has `address() == 0L`.

Writing the cell with `standardOutputCell.value(replacement)` changes the
native pointer. The replacement must be a native segment whose allocation and
lifetime satisfy the library contract. In particular, do not store a pointer
backed by an arena that can close while native code might retain it.

### Inline structs and unions

For an inline declaration such as:

```c
struct point {
  int x;
  int y;
};

extern struct point global_point;
```

bind the symbol directly with the wrapper for the aggregate:

```java
@Struct
interface Point {
  int x();
  int y();
}

var point = PointFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("global_point"));

int x = point.x();
point.y(42);
```

For `extern union number global_number`, use the generated union wrapper in
the same way:

```java
@Union
interface NumberValue {
  int integer();
  double decimal();
}

var number = NumberValueFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("global_number"));

double value = number.decimal();
```

A memory-backed interface wrapper is a live view of the global. A record
wrapper instead creates a snapshot when `reinterpret$F(...)` converts the
storage; write a changed record back explicitly with the generated
`toMemorySegment$F(...)` converter. The declared Java fields must still match
the actual native layout, including padding and pass-mode annotations.

### Fixed inline arrays

Model a fixed array with the existing indexed-field form. For example,
`extern int matrix[2][3]` can use:

```java
@Struct
interface Matrix2x3 {
  int value(
      @Sequence(2) long row,
      @Sequence(3) long column);
}

var matrix = Matrix2x3FM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F.findOrThrow("matrix"));

int item = matrix.value(1, 2);
matrix.value(1, 2, item + 1);
```

This works because the one-field wrapper layout is the inline sequence itself.
Each dimension is fixed in the generated layout and each access is bounds
checked. Unknown or sentinel-terminated arrays cannot be represented by a
fixed `@Sequence`; traverse a manually bounded `MemorySegment` according to the
native API contract instead.

### Bounded C strings

An inline string array and a pointer-valued string global have different
indirection. For `extern const char version[]`, the symbol is the first byte of
the string:

```java
long maximumVersionBytes = 128L; // bound guaranteed by the native API
var version = NativeApiFFM.SymbolLookup$F
    .findOrThrow("version")
    .reinterpret(maximumVersionBytes)
    .asReadOnly();

String text = version.getString(0L);
```

For `extern const char *version`, first read the pointer cell:

```java
var versionCell = AddressCellFM.reinterpret$F(
    NativeApiFFM.SymbolLookup$F
        .findOrThrow("version")
        .asReadOnly());

MemorySegment address = versionCell.value();
String text = address.address() == 0L ? null
    : address.reinterpret(128L).getString(0L);
```

The chosen bound must include the terminating zero byte and must not exceed the
memory region the library makes readable. If the API gives neither a size nor
a defensible maximum, a direct Java `String` binding cannot make the scan safe.
Writing a Java-created string pointer into a native global additionally needs
an explicit native allocator and ownership policy; a short-lived confined
arena is not sufficient.

### Function-pointer cells

For `extern int (*operation)(int, int)`, read the stored address through
`AddressCellFM` and create the downcall handle explicitly:

```java
static int invokeOperation() throws Throwable {
  var operationCell = AddressCellFM.reinterpret$F(
      NativeApiFFM.SymbolLookup$F.findOrThrow("operation"));

  var handle = NativeApiFFM.Linker$F.downcallHandle(
      operationCell.value(),
      FunctionDescriptor.of(
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT,
          ValueLayout.JAVA_INT));

  return (int) handle.invokeExact(19, 23);
}
```

Replacing the pointer with a Java callback requires an upcall stub. The arena
that owns that stub must remain alive for every possible native invocation.
Callback signatures, reachability, and lifetime therefore remain part of the
separate upcall/callback design rather than being inferred from a global cell.

## Practical Scope

| Library family | Representative exported data | Binding shape | Practical note |
| --- | --- | --- | --- |
| POSIX and libc | `environ`, `optarg`, `optind`, `tzname` | Pointer cells, scalar cells, and an inline pointer array | These are useful for low-level process integration, but several have thread-safety or platform-visibility constraints. See the POSIX definitions of [`environ`](https://pubs.opengroup.org/onlinepubs/9799919799/basedefs/V1_chap08.html), [`getopt`](https://pubs.opengroup.org/onlinepubs/9799919799/functions/getopt.html), and [`tzset`](https://pubs.opengroup.org/onlinepubs/9699919799/functions/tzset.html). |
| Standard streams and ncurses | `stdin`, `stdout`, `stderr`, `LINES`, `COLS`, `stdscr` | Pointer or scalar cells | Some implementations expose macros rather than named variables. ncurses explicitly says builds may replace variables with macros and recommends treating them as read-only. See [ncurses variables](https://invisible-island.net/ncurses/man/curs_variables.3x.html). |
| SQLite | `sqlite3_version[]`, `sqlite3_temp_directory` | Inline C string and writable string-pointer cell | SQLite supplies a function alternative for the version and strongly discourages direct use of the directory global because of ownership and concurrency rules. See [runtime version](https://www.sqlite.org/c3ref/libversion.html) and [temporary directory](https://www2.sqlite.org/c3ref/temp_directory.html). |
| CPython | `Py_Version`, `PyLong_Type`, `PyExc_*`, `PyOS_InputHook` | Scalar, inline object, object-pointer cells, and function-pointer cell | This is a strong globals use case in a stable native ABI, but it also demonstrates platform-width, opaque-layout, and callback-lifetime concerns. See [runtime version](https://docs.python.org/3.14/c-api/apiabiversion.html), [`PyLong_Type`](https://docs.python.org/3/c-api/long.html), and [`PyOS_InputHook`](https://docs.python.org/3/c-api/veryhigh.html). |
| Apple frameworks | Core Foundation singleton/key values and Foundation notification names | Usually const object-pointer cells | Read the stored object address, then pass it to native functions or convert it with the appropriate framework support. Examples include [`kCFBooleanTrue`](https://developer.apple.com/documentation/corefoundation/kcfbooleantrue) and [`NSCurrentLocaleDidChangeNotification`](https://developer.apple.com/documentation/foundation/nslocale/currentlocaledidchangenotification?language=objc). |
| Windows CRT and DLLs | `_environ` and application-defined exported data | Pointer cells and arbitrary exported storage | PE DLLs can export named data, but Microsoft deprecates direct `_environ` use in favor of function APIs. Ordinal-only exports cannot be requested through `SymbolLookup.find(String)`. See [DLL data exports](https://learn.microsoft.com/en-us/cpp/build/reference/export-exports-a-function?view=msvc-170) and [`_environ`](https://learn.microsoft.com/en-us/cpp/c-runtime-library/environ-wenviron?view=msvc-170). |
| Custom and vendor libraries | Version blocks, configuration cells, counters, descriptor tables, singleton objects, and callback hooks | Any of the forms above | This is the most likely source of globals with no function alternative. The library's header and ABI documentation remain the authority for layout, mutability, lifetime, and synchronization. |

## Exclusions and Hazards

The current recipes intentionally do not hide these constraints:

- **Macros and constants:** preprocessor macros, enum constants, and inline
  functions do not create data symbols. `SymbolLookup$F` can only find a name
  exported by the loaded binary.
- **Hidden data:** `static` variables, hidden-visibility symbols, and symbols
  absent from the selected library lookup are not bindable by name.
- **Thread-local storage:** an exported thread-local address cannot safely be
  resolved once and cached as process-global storage. `errno` is commonly a
  macro backed by thread-local state; use `@CallState` for error capture rather
  than treating it as an ordinary global.
- **Ownership and lifetime:** pointer cells can refer to library-owned,
  caller-owned, immortal, replaceable, or transient storage. A binding cannot
  infer which allocator may replace or free it.
- **Unknown extents:** sentinel-terminated arrays such as `environ` need an
  explicit traversal rule and a defensible memory bound. A symbol contains no
  length metadata.
- **Callbacks:** a function-pointer setter must keep its upcall stub and any
  captured Java state alive for as long as native code can call it.
- **Constness:** lookup does not report C `const`. `asReadOnly()` is an explicit
  Java-side safeguard, not native type discovery or a native memory-protection
  guarantee.
- **Concurrency:** plain generated getters and setters perform plain memory
  access. They do not imply C `volatile`, C atomics, Java volatile ordering, or
  safe concurrent mutation. Use a library API or an explicitly designed
  `VarHandle` access policy when ordering matters.
- **C ABI widths:** Java primitive layouts do not by themselves model every C
  ABI type. `long`, `unsigned long`, `size_t`, `wchar_t`, packing, and custom
  alignment must be resolved before binding globals that use them. For example,
  CPython's `Py_Version` is an `unsigned long`, whose width differs between
  common Unix and Windows ABIs.
- **C++ names and objects:** mangling is compiler-specific, and non-trivial C++
  objects can require construction, destruction, and access functions. A
  discoverable mangled data symbol is not by itself a stable binding contract.
- **Ordinal-only Windows exports:** `SymbolLookup.find(String)` accepts a name,
  not a PE export ordinal.

Incorrect bounds, layouts, or lifetimes can corrupt native memory or crash the
JVM. A successful symbol lookup proves only that a name resolved, not that the
chosen Java layout is correct.

## Deferred Alternatives

### Typed `@Global` properties

Zero-argument getters and one-argument setters would be attractive for scalar
and pointer cells:

```java
@Global int counter();
@Global void counter(int value);
```

The apparent simplicity breaks down for inline versus pointer aggregates,
unknown arrays, strings, callbacks, ownership, constness, and platform ABI
types. No such annotation is introduced until those distinctions can remain
explicit.

### Lookup helper

A helper combining `findOrThrow(name)` with `reinterpret(layout.byteSize())`
would save one expression but add little capability. Existing generated
wrappers already own the useful layouts, and a local helper is trivial when a
binding contains enough globals to need it.

### Global-backed wrapper types

A new annotation could bind an `@Struct` or `@Union` wrapper instance directly
to a named symbol. This would maximize reuse of field generation, but it would
also create extra cell types for simple scalars and couple otherwise reusable
memory shapes to one library and symbol.

### Storage-first generated accessors

OpenJDK jextract exposes the global storage segment and layout, then adds typed
scalar, composite, and indexed conveniences where the C declaration supplies
enough information. Its general
[global accessor model](https://cr.openjdk.org/~mcimadamore/panama/jextract_changes.html)
is the preferred direction if first-class support becomes justified.

Any future Alveolo design should preserve the raw, correctly bounded storage
segment as an escape hatch. Typed conveniences should be layered on that
storage rather than pretending every exported datum is a Java property.

## Revisit Criteria

Reconsider first-class global generation when at least one of these is shown by
a real binding:

1. Lookup, reinterpretation, and wrapper construction cause substantial
   repeated boilerplate that local helpers do not address cleanly.
2. A required exported-data shape cannot be expressed safely with
   `SymbolLookup$F`, current memory wrappers, and explicit FFM access.
3. Generated const, thread, atomic-access, or lifetime policy can provide a
   concrete safety guarantee unavailable to the manual composition.

Until then, keeping global access explicit is smaller, more predictable, and
more honest about the native ABI contract.
