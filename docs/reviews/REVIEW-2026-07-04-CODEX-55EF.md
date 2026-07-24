# alveolo-ffm Review

Saved: 2026-07-04

## Verification

`mvn clean install` passed.

- Processor tests: 48/48
- Benchmark/native tests: 26/26
- Worktree was clean after verification

## Findings

### P1: ✅ Record address fields can store dangling native pointers

`ForeignMemoryProcessor` knows nested record-address fields need allocator
ownership, but the public two-argument conversion still opens and closes a
confined arena before returning, and the default setter uses `Arena.ofAuto()`
with no owner tied to the parent segment.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:699`
- `processor/src/test/resources/memory/passmode/FieldModesFM.java:58`
- `processor/src/test/resources/memory/passmode/FieldModesFM.java:116`

Recommendation:

Make allocator ownership explicit for record-address setters, and either remove
or deprecate the unsafe overload, or have it fail when address fields require
owned backing memory.

### P1: ✅ Plain `String` returns are accepted but not implemented

`String` maps to `ValueLayout.ADDRESS`, passes return validation, then
`ExecutableGenerator` emits a raw `invokeExact(...)` return instead of
converting the returned `MemorySegment` to a Java string. The old `LibCFFM`
fixture has the desired conversion shape commented out.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:111`
- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:219`
- `processor/src/test/resources/interface/LibCFFM.java:122`

Recommendation:

Either generate `MemorySegment` to UTF-8 conversion for plain C string returns,
or reject non-`@CFString` `String` returns with a clear compile error.

### P2: ✅ `@Struct(name = ...)` and `@Union(name = ...)` simple-name overrides look broken

The annotations expose name overrides, but `foreignClassName` returns simple
override names directly while `ForeignMemoryProcessor` passes that name to
`createSourceFile` even though it writes the original package. The
`DispatchTableProcessor` has a package-prefix fix that memory generation lacks.

Evidence:

- `core/src/main/java/org/alveolo/ffm/Struct.java:17`
- `processor/src/main/java/org/alveolo/ffm/processor/ProcessorUtils.java:13`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:418`
- `processor/src/main/java/org/alveolo/ffm/processor/DispatchTableProcessor.java:127`

Recommendation:

Centralize generated class-name resolution so simple overrides are resolved
relative to the source package and qualified overrides are left intact.

### P2: ✅ Struct accessor validation is too permissive

Setter-looking methods are not checked against getter type or fluent return
type, and setter-only fields are silently dropped. That can turn a user mistake
into generated source that fails later or does not implement the interface.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:198`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:204`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:215`

Recommendation:

Validate getter/setter pairs explicitly: getter return type, setter parameter
type, fluent return type, and presence of at least one getter for every inferred
field.

### P3: ✅ Cleanup and build hygiene issues

Several smaller issues add friction without currently breaking the build.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignInterfaceProcessor.java:168`
- `core/src/main/java/org/alveolo/ffm/Library.java:43`
- `processor/src/test/java/org/alveolo/ffm/processor/ForeignInterfaceProcessorTest.java:11`
- `benchmark/pom.xml:60`

Details:

- ✅ `@Library` validation mentioned a non-existent `DEFAULT_LOOKUP` kind.
- ✅ One processor test prints native canonical layouts during every build.
- ✅ Benchmark compilation used deprecated `forceJavacCompilerUse`.
- ✅ Benchmark shading emits module-info warnings.

## Improvement Targets

- ✅ Add focused compile tests for plain `String` returns, generated-name
  overrides, invalid accessor pairs, and unsafe record-address conversion.
- ✅ Share generated class-name policy across processors without broad helper
  churn.
- Keep runtime FFM tests in the benchmark module, but add only the smallest
  native cases that prove a behavior.
- Consider adding `git diff --check` to the normal validation loop for generator
  changes.

## Useful Next Features

- ✅ `size_t`, `ssize_t`, and C `long` canonical-layout support.
- Explicit C string return ownership, probably through a dedicated annotation.
- ✅ `errno` or call-state capture.
- Pointer-to-pointer out parameters.
- Upcall/callback generation.
- Packed/aligned struct options.
- Richer platform library lookup for app-local native libraries and frameworks.
