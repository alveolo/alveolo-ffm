# Code Review — alveolo-ffm

*Comprehensive review of core, processor, benchmark modules, tests, POMs, and CI.
Date: 2026-07-02.*

Overall this is a clean, well-scoped project: annotations are minimal, generated
code is readable plain Java, expected-output tests are a great choice, and error
handling in generators (the `hasErrors` → throwing placeholder pattern) is
thoughtful. The findings below are ordered by severity.

## Bugs

### 1. ✅ Unions get struct-style padding, producing layouts that disagree with C — confirmed

`ForeignMemoryProcessor.writeLayout` wraps fields in `ForeignUtils.pad(...)` for
both `structLayout` and `unionLayout`, but `pad()`
(`core/src/main/java/org/alveolo/ffm/ForeignUtils.java:202`) computes padding by
accumulating offsets sequentially — struct semantics. For a union all members
start at offset 0; what a union actually needs is its *size rounded up to its
alignment*.

Verified in jshell:

```java
unionLayout(sequenceLayout(5, JAVA_BYTE), JAVA_INT)
// => size 5 / align 4  (C: sizeof == 8)
sequenceLayout(n, thatUnion)
// => IllegalArgumentException: Element layout size is not multiple of alignment
```

Unions need a dedicated path: no inter-member padding, plus a trailing pad
member of `roundUp(maxSize, maxAlign) - maxSize` (or equivalent).

### 2. ✅ `String` return types silently generate non-compiling code

In `ExecutableGenerator.invoke`
(`processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:219`)
the `isString()` return branch emits the raw `invokeExact` call with no cast or
conversion (`// TODO`), and `checkParameterTypes` doesn't reject it because
`String` maps to `ValueLayout.ADDRESS`. So `String l64a(long n)` produces a
generated file that won't compile, with a confusing javac error instead of a
processor diagnostic. Either implement the conversion (the commented-out `l64a`
block in the `LibCFFM` test fixture is exactly the needed code) or `printError`
until it's supported.

### 3. ✅ Struct interface fields with unsupported accessor shapes are silently dropped — layout corruption

In `inferFields`
(`processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:215`),
when a method group has no valid getter (`accessor == null`) the field is
skipped with `continue` and no diagnostic. A field declared with only a setter,
or with a `void` setter (the setter check at line 204 only accepts DECLARED
returns and is itself a TODO), vanishes from the layout. The struct then has the
wrong size and every subsequent field the wrong offset — silent native memory
corruption. This should be a hard error.

### 4. ✅ Array-typed struct fields generate wrong layout and broken accessors

The `ARRAY` branch in `inferFields` (`ForeignMemoryProcessor.java:223`) is an
empty TODO, so an `int[]` field falls through with `sequence = 1`:
`primitiveLayout` maps `"int[]"` to `sequenceLayout(1, JAVA_INT)` (ignoring any
`@Sequence`), and `writeAccessorsSimple` emits `(int[]) FM$VH$x.get(ms)`, which
doesn't compile. The intended error ("use the corresponding Buffer class")
should actually be emitted.

### 5. ✅ Records with buffer/`@Sequence` fields generate non-compiling converters

`writeStaticAccessorsBuffer` (`ForeignMemoryProcessor.java:986`) is an empty
TODO, but `writeRecordConverters` (`ForeignMemoryProcessor.java:693`) still
emits `fromMemorySegment`/`toMemorySegment` calling `<name>(ms)` for every
record component — accessors that were never written. A record like
`@Struct record Foo(IntBuffer data)` produces uncompilable output with no
diagnostic.

### 6. ✅ `@Struct(name = ...)` breaks package placement

`ProcessorUtils.foreignClassName`
(`processor/src/main/java/org/alveolo/ffm/processor/ProcessorUtils.java:26`)
returns the raw override name (its own TODO notes the simple-vs-qualified
inconsistency), and `writeFile` (`ForeignMemoryProcessor.java:414`) passes it to
`createSourceFile` unqualified while still writing `package <pkg>;` in the
header — a wrong-package compile error for any annotated type that isn't in the
default package. `DispatchTableProcessor.generatedClassName` already solves this
correctly (prepends the package when the name has no dot); the same logic
belongs in the struct/union path.

Also in `foreignClassName`: the `@Symbol` branch is dead code — `@Symbol`
targets `METHOD` only, so it can never be present on a `TypeElement`. Given
commit d4caa5 says "use @Symbol for binding to native", it looks like `@Symbol`
was meant to gain a `TYPE` target and never did.

### 7. Nested-record address setter uses `Arena.ofAuto()` — dangling pointer risk

`nestedAddressValue` (`ForeignMemoryProcessor.java:1215`) allocates a segment
from an auto arena and stores its address into native memory. Nothing in Java
keeps that segment reachable (a pointer inside native memory is invisible to the
GC), so the allocation can be reclaimed while native code still holds the
pointer — a use-after-free that will reproduce rarely and be brutal to debug.
The generated allocator-taking overload is the safe variant; remove the
auto-arena one or make it keep a Java-side reference to the segment.

### 8. Vtable pointer is captured once at construction

The generated constructor (`writeConstructors`,
`ForeignMemoryProcessor.java:798`) reads `ff$vtbl` and builds the dispatch
wrapper eagerly. If the wrapper is constructed before native code initializes
the object (a common pattern: allocate, pass to native init, then call), all
virtual calls go through a stale/null vtable. Worth either documenting loudly or
reading the vtable lazily.

## Inconsistencies

- **Leftover debug logging**: `ForeignInterfaceProcessor.process`
  (`ForeignInterfaceProcessor.java:31`) prints `printNote` for every round,
  every annotation, and every element, including the full `rootElements` list.
  Every downstream consumer's build log gets this spam. The other two processors
  are silent — delete these.
- **Stale error message**: `ForeignInterfaceProcessor.java:170` says "unless
  kind is DEFAULT_LOOKUP", but `Library.Kind` has no such constant.
- **Package derivation**: `ForeignMemoryProcessor` correctly uses
  `Elements.getPackageOf`, while `ForeignInterfaceProcessor` and
  `DispatchTableProcessor` do `lastIndexOf('.')` on the qualified name — wrong
  for nested interfaces (`a.b.Outer.Inner` → "package" `a.b.Outer`).
- **Method filtering**: `ForeignMemoryProcessor` selects methods by `ABSTRACT`
  modifier; `ForeignInterfaceProcessor` filters out only `default`/`static`, so
  a `private` interface helper method would get a generated native binding.
- **`VALUE_LAYOUT_NOT_SUPPORTED`** (`TypeGenerator.java:27`) is a
  `public static` *non-final* `String` compared by `==` in two places. It works
  today, but it's an accident waiting for a refactor. Make it final at minimum;
  better, return `Optional<String>` or null and test explicitly.
- **`Slot.value()` vs `Slot.index()`**: two attributes meaning the same thing,
  plus ~40 lines of validation (`hasSlotValue` mirror-walking) to police them.
  Unless there's a planned semantic difference, one attribute would delete real
  complexity. `@Virtual` gets by with just `value()`.
- **Error duplication for virtual methods**: `writeVirtualMethod` constructs an
  `ExecutableGenerator` (which prints parameter-type errors) and the generated
  `*Vtbl` interface is then processed by `DispatchTableProcessor`, which prints
  the same errors again.

## Code duplication

- **Buffer-type mapping exists in three places**: `NIO_BUFFER_TYPES` +
  `extractBufferElementType` in `ForeignMemoryProcessor` (with its double
  switch through wrapper classes), `elementLayout()` in `VariableGenerator`, and
  the buffer cases of `primitiveLayout()` in `TypeGenerator`. One shared
  enum/map (`buffer class ↔ primitive ↔ ValueLayout constant`) would replace all
  of it — `extractBufferElementType` alone would shrink from ~30 lines to a
  lookup.
- **`writeAccessorsSimple` vs `writeStaticAccessorsSimple`**
  (`ForeignMemoryProcessor.java:886–1090`) are ~200 lines that differ only in
  `static`/`this` and the fluent return. A single template parameterized on
  receiver would halve it — and would have prevented bug 5 (the record/buffer
  gap) by construction.
- **The `process()` loop + `Throwable` → StringWriter → `printError`
  scaffolding** is copy-pasted across all three processors; a small shared base
  class would also unify the filtering inconsistencies above.
- **The suppressed-exception chaining pattern** in `ForeignUtils`
  (`ForeignUtils.java:97`) appears three times; a tiny `tryLoad` helper
  accumulating into one exception would read better.

## Infrastructure

- **The deploy pipeline targets a dead service.** `parent/pom.xml` and the CI
  deploy job publish to `oss.sonatype.org` (legacy OSSRH), which Sonatype shut
  down at the end of June 2025. Deploys can't be working. Migration to the
  Central Portal (`central-publishing-maven-plugin`) is required to release —
  which matters, since the README tells users to depend on `0.0.2-SNAPSHOT`
  that isn't resolvable from any public repo.
- ✅ `benchmark/dependency-reduced-pom.xml` (a shade-plugin artifact) is committed;
  add it to `.gitignore`.
- Minor: JUnit 5.11.1 and compile-testing 0.23.0 have newer releases.

## Feature ideas, roughly in value order

1. **Upcalls / function pointers** — the biggest gap for real bindings (qsort
   comparators, callbacks, event handlers). A `@Callback` functional-interface
   annotation generating the `upcallStub` glue would fit the project's style
   perfectly.
2. **`errno`/`GetLastError` capture** —
   `Linker.Option.captureCallState("errno")` behind something like
   `@CaptureErrno`, exposing the value via a thread-local or an out-record.
   Without it, most libc bindings can't report errors.
3. **String returns and `size_t`** — both already TODO'd in the code
   (`Linker.canonicalLayouts().get("size_t")` is referenced in a fixture
   comment). These unblock a large share of POSIX APIs.
4. **`Linker.Option.critical()`** — an opt-in `@Critical` for hot, short calls;
   the benchmark module can prove the win, and heap-buffer/array params could
   then use `critical(true)` to pass heap memory without copying.
5. **Global variable bindings** — exported data symbols (`environ`, `stdout`)
   as generated static accessors.
6. **Nested struct/union arrays** — TODO'd in `TypeGenerator`; needed for
   real-world C structs (`char name[32]` inside a struct is bug 4's use case).
7. **Null-safety story for strings/segments** — currently `strlen(null)` NPEs
   inside generated code; C APIs routinely accept NULL. An `@Nullable` mapping
   to `MemorySegment.NULL` would be cheap.
8. **C enum mapping** — `@Enum`-annotated Java enums marshalled as `int`.
9. **Packed/aligned structs** — `@Struct(packed = true)` or per-field alignment
   override; `pad()` is already the single place that would change.
10. **COM ergonomics on Windows** — `@DispatchTable` is 80% of a COM story;
    IUnknown helpers and HRESULT-to-exception mapping would make it a
    distinctive feature (only JNA really offers this today).

## Suggested priorities

The most impactful short-term work: fix the silent-breakage bugs (1, 3, 4, 5 —
they violate the project's own "predictable generated code" promise by failing
without a processor diagnostic), delete the debug notes, and sort out Central
publishing so the README instructions actually work.
