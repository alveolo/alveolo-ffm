# Independent Code Review: alveolo-ffm

Date: 2026-07-06

Scope: independent review of the repository, focusing on bugs, inconsistencies,
duplication, improvement opportunities, and useful next features.

Note: existing `REVIEW*` files were intentionally not consulted.

## Findings

### P1: ✅ NIO buffer struct fields generate the wrong layout

Buffer accessors extract `@Sequence`, then replace the field type with the
primitive element type, but `TypeGenerator.layout()` ignores `sequence` for
primitive fields. A valid field such as:

```java
@org.alveolo.ffm.Sequence(3)
java.nio.IntBuffer data();
```

generates a single `ValueLayout.JAVA_INT` field, while the accessors copy/read
3 ints from a 4-byte slice. This is a runtime/layout bug, not a compile-time
failure.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:267`
- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:77`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:1497`

Suggested fix: preserve enough field metadata to emit
`MemoryLayout.sequenceLayout(sequence, elementLayout)` for buffer fields, and add
a golden fixture plus runtime test for a valid fixed-size buffer field.

### P1: ✅ Partial benchmark reactor build cannot resolve the processor

`mvn -pl benchmark -am package -DskipTests` fails because the benchmark module
uses `alveolo-ffm-processor` only in `annotationProcessorPaths`. Maven reactor
selection does not treat that as a normal project dependency, so `-am` includes
`core` but not `processor`.

Relevant code:

- `benchmark/pom.xml:61`

Observed failure:

```text
Resolution of annotationProcessorPath dependencies failed:
org.alveolo.ffm:alveolo-ffm-processor:jar:0.0.2-SNAPSHOT (absent)
```

Suggested fix: add the processor as a provided/optional dependency in benchmark,
or document that partial benchmark builds require installing the processor first.
The cleaner build fix is to make Maven's reactor aware of the module.

### P2: Inherited abstract methods are ignored

`@ForeignInterface`, `@Struct`, and `@DispatchTable` processors inspect only
`getEnclosedElements()`. Interfaces that extend a base interface can therefore
generate incomplete implementations or omit inherited struct fields/dispatch
slots.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:124`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:211`
- `processor/src/main/java/org/alveolo/ffm/processor/DispatchTableProcessor.java:129`

Suggested fix: either explicitly reject inherited abstract members with a clear
diagnostic, or use `Elements.getAllMembers(...)` and filter carefully for
abstract interface methods while preserving deterministic ordering.

### P2: User names can collide with generated locals

Generated method bodies use fixed names such as `ff$arena`, `ff$e`, `ff$t`,
`ff$ms$<name>`, and related temporaries. Java permits `$` in identifiers, so a
user parameter named `ff$e`, `ff$arena`, or a name that maps to generated
array/buffer temporaries can produce invalid generated source.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:110`
- `processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:294`

Suggested fix: reject source identifiers using reserved generated prefixes such
as `ff$`, `FF$`, and `FM$`, or synthesize collision-proof names from a reserved
name allocator.

### P2: Nested annotated types are likely broken

Generated class names and `implements` clauses use simple names. A nested source
type such as `Outer.Inner` would tend to generate a top-level `InnerFFM
implements Inner`, which is not generally resolvable from the package scope.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ProcessorUtils.java:79`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:76`

Suggested fix: add tests for nested annotated types. If support is not intended,
reject them explicitly; if support is intended, generate safe top-level names and
refer to source types by qualified or canonical nested names.

### P3: ✅ Processor module is not JPMS-service friendly

The processor jar includes `META-INF/services`, but `module-info.java` does not
declare a JPMS `provides javax.annotation.processing.Processor with ...`.
Processor discovery can fail when the processor is placed on a processor module
path.

Relevant code:

- `processor/src/main/java/module-info.java:1`
- `processor/src/main/resources/META-INF/services/javax.annotation.processing.Processor:1`

Suggested fix: add a `provides` clause for all three processors if JPMS
processor-path usage is supported.

### P3: Diagnostic references a non-existent enum value

The diagnostic says:

```text
@Library value is required unless kind is DEFAULT_LOOKUP
```

but `Library.Kind` has only `NAME`, `PATH`, and `FRAMEWORK`.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:168`
- `core/src/main/java/org/alveolo/ffm/Library.java:43`

Suggested fix: update the message, or add an explicit default-lookup kind if that
is a planned API direction.

### P3: ✅ A test prints runtime layout data without asserting behavior

`ForeignInterfaceProcessorTest.x()` dumps canonical layouts to stdout and has no
assertions. This adds noisy CI output without guarding behavior.

Relevant code:

- `processor/src/test/java/org/alveolo/ffm/processor/ForeignInterfaceProcessorTest.java:11`

Suggested fix: remove it, disable it, or turn it into a named assertion-based
test.

## Improvement Themes

### Centralize type and layout mapping

Primitive, array, buffer, and layout mapping logic is spread across:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:107`
- `processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:269`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:55`

A small shared descriptor, for example `ForeignType`, would reduce duplication
and make bugs like the buffer-field layout issue harder to introduce.

### Reduce duplicated accessor generation

Static and instance memory accessors contain large parallel branches for nested
value, nested address, primitive, and unsupported fields.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:1109`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:1339`

Consider generating accessor bodies from shared field-operation descriptions,
then wrapping them in static or instance method shells.

### Replace string sentinel checks

Unsupported layouts are represented by the string constant
`VALUE_LAYOUT_NOT_SUPPORTED`, and checks use reference equality against that
constant. This works only because the same constant instance is returned.

Relevant code:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:27`
- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:477`
- `processor/src/main/java/org/alveolo/ffm/processor/MemoryLayoutGenerator.java:33`

A small result type or enum would be more robust and clearer.

### Align documentation style

The project instructions call for Markdown style JavaDoc. Most public docs use
`///`, but `Sequence.java` still uses block JavaDoc.

Relevant code:

- `core/src/main/java/org/alveolo/ffm/Sequence.java:11`

## Useful Next Features

- ABI-native layouts for `size_t`, `ssize_t`, C `long`, unsigned integers,
  pointer-sized integers, and platform-specific canonical layouts.
- Fixed-size array fields in structs/unions, including arrays of nested structs.
- Nullable and ownership controls for returned C strings and pointer values,
  including custom releasers.
- `errno` and `GetLastError` capture support.
- Upcalls, callbacks, and function pointer fields.
- Better library lookup controls: lazy symbol resolution, optional libraries,
  always-include default lookup, and user-supplied `SymbolLookup`.
- More validation around generated-name collisions and unsupported source shapes.

## Verification

Commands run during review:

```text
mvn -pl processor -am test
```

Result: passed, 68 tests.

```text
mvn test
```

Result: passed, 94 tests total.

```text
mvn package -DskipTests
```

Result: passed, with shade warnings.

```text
mvn -pl benchmark -am package -DskipTests
```

Result: failed due to the benchmark module not pulling
`alveolo-ffm-processor` into the selected reactor.
