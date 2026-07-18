# Independent Review of alveolo-ffm

Reviewed on 2026-07-09.

This review was performed without reading `TODO.md`, previous review artifacts,
or Git history.

The project is small and readable, and the normal build is healthy, but the
review found two serious runtime defects—including one JVM crash—plus several
annotation-processing edge cases that produce invalid source.

## Findings

### 1. Critical — Newly Allocated Vtable Structs Can Crash the JVM

The generated allocator constructor creates zero-filled storage, then
immediately reads the null vtable pointer and binds every virtual method from
it. The subsequent native memory read at address zero produced a reproducible
`SIGSEGV`, exit code 134—not a Java exception.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:825`
- `processor/src/main/java/org/alveolo/ffm/processor/DispatchTableProcessor.java:229`

Resolve the table lazily, check for `MemorySegment.NULL` before
reinterpretation, and probably separate `allocate()` from a
`wrapInitializedNativeObject()` factory. An allocator constructor is unsafe
unless it also requires the initial vtable.

### 2. ✅ High — Typed Struct-Buffer Access Uses the Wrong Byte Order

Generated code calls `asByteBuffer().asIntBuffer()` without setting native byte
order. `MemorySegment` indexed access uses native order, while the buffer view
is big-endian by default. On a little-endian system, writing `0x01020304`
through the indexed accessor returned `0x04030201` through `data().get(0)`.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:1198`

Set `.order(ByteOrder.nativeOrder())` before creating typed views and add
runtime tests for every buffer type.

### 3. Medium — Inherited Abstract Methods Generate Uncompilable Implementations

All three processors inspect `getEnclosedElements()` rather than inherited
members. A generated class implementing a child interface consequently fails
to implement its parent's methods.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:126`
- `processor/src/main/java/org/alveolo/ffm/processor/DispatchTableProcessor.java:133`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:185`

Either support inheritance using `Elements.getAllMembers()` plus override
resolution, or reject interfaces with inherited abstract methods using one
clear processor diagnostic. The README acknowledges the limitation for foreign
interfaces, but producing invalid generated code is still undesirable.

### 4. ✅ Medium — Legal `@Sequence` Placement Can Generate Broken Var Handles

A primitive accessor annotated `@Sequence(2)` still receives a scalar layout,
but var-handle generation assumes the sequence adds an index coordinate. The
fixture compiled successfully and then threw `WrongMethodTypeException` on its
first setter call.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:893`
- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:107`

Validate that `@Sequence` is used only on arrays and buffers, or implement
actual scalar-array field semantics.

### 5. Medium — Generated Names Are Not Hygienic

Legal parameter names such as `ff$arena` and `ff$ms$value` collide with
generated locals. Both cases produced compilation failures. Generated
identifiers are derived directly from parameter names, while `ff$arena` is a
fixed local name.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:268`
- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:204`

Introduce a method-scoped name allocator that reserves source parameter names
and creates guaranteed-unique synthetic names.

### 6. Medium — `@Symbol` Values Are Not Escaped in Generated Java

A legal `@Symbol("quote\"inside")` generated malformed Java because the value
is inserted directly into a string literal. Backslashes can also silently
change the symbol being resolved. Library values have partial escaping but do
not handle control characters.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:136`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:273`

Use one shared, complete Java string-literal encoder for every
annotation-derived string.

### 7. Medium — Combining `@Struct` and `@Union` Produces Internal Stack Traces

The processor independently handles both annotations and attempts to recreate
the same `*FM` file, producing duplicate `FilerException` stack traces rather
than a useful diagnostic.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:93`

Validate mutual exclusion before opening any output and process each annotated
type only once.

### 8. Safety Concern — Returned Native Strings Are Scanned Without a Bound

Both ordinary strings and CFStrings reinterpret pointers to `Long.MAX_VALUE`
before scanning. A missing terminator can read well outside the intended
allocation and potentially crash the process.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:288`
- `core/src/main/java/org/alveolo/ffm/macos/CFStringSupport.java:78`

Add a bounded-string annotation or configurable maximum, and prefer APIs that
provide an explicit length.

### 9. ✅ Documentation Contradicts Enforced Setter Behavior

The README declares struct-interface setters such as
`timeval tv_sec(int value)`, but the processor deliberately reports every
non-getter declaration as unsupported. Generated classes provide extra fluent
setters; source interfaces must currently declare getters only.

Relevant code:

- `README.md:243`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:207`

Either support declared setters or correct the example.

### 10. C ABI Portability Is Underspecified

Java primitives map directly to `ValueLayout.JAVA_*`. This works for
fixed-width assumptions but not general C types: notably, C `long` is 64-bit on
LP64 systems and 32-bit on Windows LLP64. `size_t`, `wchar_t`, unsigned types,
packed structures, and explicit alignment are also missing. Bindings that
appear portable can therefore describe the wrong ABI.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:192`

## Smaller Inconsistencies

- Invalid generated-name diagnostics are attached to the annotation type
  rather than the user declaration because processors call
  `validateSimpleClassName(annotation, ...)` instead of passing the annotated
  type.
- Unknown operating systems are silently treated as Linux in
  `ForeignUtils.os()`.
- Named-module instructions are incomplete: generated code needs native access
  for the application module, while library loading and CFString support
  execute restricted operations from `org.alveolo.ffm` itself.
- `java.logging` is required by the processor module but unused.
- The long-division benchmark uses an `int` mask,
  `(random.nextInt() & 0xFFFFFFFF) + 1`, rather than a long mask. It can remain
  negative and has a tiny chance of producing zero.
- `${uberjar.name}` is referenced in `benchmark/pom.xml` but never defined.

## Duplication and Maintainability

The main structural issue is that validation, semantic analysis, and source
emission are intertwined.

- The three processor `process()` implementations duplicate annotation
  iteration, exception formatting, file creation, and diagnostics.
- Java source is assembled through many independent text templates and
  replacements, which led directly to escaping and name-collision bugs.
- Large golden generated-source files duplicate generator output and test
  formatting more strongly than runtime behavior.
- Field validation occurs partly before generation and partly while emitting
  layouts and accessors, making it easy for accepted declarations and generated
  behavior to diverge.

A useful refactor would build an immutable intermediate model first—validated
type, fields, native methods, layouts, ownership, and transfer modes—then emit
source only if that model is valid. A small `SourceWriter`, string-literal
encoder, and synthetic-name allocator would remove much of the repeated fragile
code.

## Recommended Next Features

In priority order:

1. Explicit C ABI types and layout overrides: `CLong`, `SizeT`, unsigned types,
   platform canonical layouts, packing, and alignment.
2. Nullable pointer and ownership semantics, including bounded or owned
   returned strings and explicit borrowed or owned native objects.
3. Upcalls and callbacks, including callback lifetime management.
4. `Linker.Option` support
  * variadic calls
  * ✅ captured call state or `errno`
  * critical-call options
5. ✅ Fixed arrays of nested structs and multidimensional sequences.
6. Fixed arrays of Enum and bitfield support remain open.
7. Lazy or optional symbol resolution so one unavailable symbol does not
   prevent using an otherwise valid binding.
8. Injectable `Linker` and `SymbolLookup` factories for testing and nonstandard
   loading environments.

## Verification

- ✅ `mvn clean install` completed successfully.
- ✅ Processor tests: 74, with 1 skipped.
- ✅ Native and benchmark integration tests: 26 successful.
- ⚠️ The core module has no tests.
- ✅ Focused external fixtures reproduced the vtable crash, endian corruption,
  inherited-method failures, scalar-`@Sequence` failure, symbol escaping,
  generated-name collisions, and dual-annotation failure.
- ✅ No existing repository files were modified during the review.
