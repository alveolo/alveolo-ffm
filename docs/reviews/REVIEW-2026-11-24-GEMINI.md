# alveolo-ffm Review

Saved: 2026-11-24

## Verification

`mvn test` passed across all modules:

- Core tests: 5/5
- Processor tests: 237/237 (2 skipped)
- Benchmark/native tests: 97/97

## Findings — Processor

### P1: `ClassCastException` processor crash on any `TypeVariable`

`TypeGenerator` casts `types.asElement(typeMirror)` directly to `TypeElement`.
For any type parameter (e.g., `<T> void call(T value)`,
`@Struct interface Box<T> { T value(); }`, `@Struct record Box<T>(T value) {}`,
`@DispatchTable interface Vtbl<T> { @Slot(0) T get(); }`),
`types.asElement(typeMirror)` returns a `TypeParameterElement`, crashing the
processor with a `ClassCastException` stack trace and aborting generation of
the enclosing type.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:113`

Recommendation:

Guard the cast (`types.asElement(typeMirror) instanceof TypeElement te ? te : null`)
and report a clean compile-time diagnostic on the offending declaration while
continuing to emit compilable Java for unaffected members.

### P1: `ClassCastException` processor crashes on unresolved annotation values

In `javac`, an unresolved class literal or constant in an annotation (such as
`@Require(Missing.class)`, `@Struct(symbols = Missing.class)`, or
`@Sequence(MISSING)`) yields `String` (`"<error>"`) from
`AnnotationValue.getValue()`. Direct casts to `TypeMirror` or `Long` throw
`ClassCastException` and abort processing of the enclosing type.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/BindingAnnotations.java:235`
- `processor/src/main/java/org/alveolo/ffm/processor/ObjectMethodsGenerator.java:265`
- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:387`

Recommendation:

Use `instanceof` checks on `AnnotationValue.getValue()` and skip or report a
clean diagnostic instead of crashing.

### P1: `AssertionError` crash on `@Flexible("count")` with invalid canonical scalar on `byte`/`short`/`char`

`validateFlexibleFields` runs before `canonicalScalarError()` and accepts any
integer primitive (`byte`, `short`, `char`, `int`, `long`). For declarations
such as `@SLong short count(); byte data(@Flexible("count") int i);`,
`validateFields` reports `@SLong requires Java long` but leaves `flexible`
populated. `FlexibleArrayGenerator.upperBound()` then switches only on
`INT`/`LONG` and throws `AssertionError`.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAnalyzer.java:500-526`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAnalyzer.java:620-635`
- `processor/src/main/java/org/alveolo/ffm/processor/FlexibleArrayGenerator.java:115-125`

Recommendation:

Invalidate or skip flexible bound generation when the count field has
validation errors (or check canonical scalar compatibility before binding
`flexible`), so the type still compiles cleanly for unaffected members.

### P1: `releaseGuards()` generates uncompilable Java when the `@Release` target method has errors

When a `@Release` target method (or `CFRelease`) fails validation,
`methodWithHandle()` emits `throwingMethodPlaceholder()` without declaring
`MethodHandle$<index>$F`. Unaffected methods referencing that release method
still emit `if (MethodHandle$<index>$F == null) ...` based solely on
`methods.indexOf(target)`, causing `cannot find symbol` errors in the generated
source and violating the unaffected-members compilation contract.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:217-243`

Recommendation:

Check whether the target release method has errors (or has no emitted
`MethodHandle$N$F`) before referencing its handle in `releaseGuards()`.

### P1: `@Virtual` bridge silently drops `@In`, `@Out`, and `@Sequence` on qualified parameter types

`@In`, `@Out`, and `@Sequence` target `{PARAMETER, TYPE_USE}`. Per JLS §9.7.4,
when placed before a qualified type name (e.g., `@Out java.nio.IntBuffer`,
`@In @Sequence(4) java.nio.ByteBuffer`, `@In @Sequence(2) pkg.Point[]`),
`javac` attaches them only to `VariableElement`, not to `TypeMirror`.
`VariableGenerator.bridgeSignature()` prints only `bridgeTypeName() + " " + name()`,
omitting element-level annotations on the generated `*V` bridge interface. As a
result, `@Out` is silently lost (copying uninitialized Java buffers into native
memory), `@In` is lost (rejecting read-only buffers and mutating caller buffers
on return), and `@Sequence` is lost (dropping length validation or failing
`@Value` compilation).

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:54-56`
- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:174-218`

Recommendation:

Forward parameter-level `@In`, `@Out`, and `@Sequence` annotations from
`VariableElement` into `bridgeSignature()`.

### P1: `@Union` bypasses `StructInterfaceModel`: silent miscompilation of `@Virtual`/`@Symbol` and broken inheritance

Because `isStructInterface` is false for `"union"`:

1. `@Virtual` and `@Symbol` on `@Union` interface methods are never rejected
   and are silently compiled as union memory fields (even `@Virtual(-1)`
   compiles without error).
2. `inferFields` only inspects `getEnclosedElements()`, so
   `@Union interface B extends A` fails to implement inherited methods from `A`,
   producing raw `javac` errors with no processor diagnostic.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryGenerator.java:47-55`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAnalyzer.java:56-72`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:127-164`

Recommendation:

Explicitly reject `@Virtual`, `@Symbol`, and interface inheritance (or handle
them consistently) on `@Union` with clear processor diagnostics.

### P1: `reinterpret$F(MemorySegment.NULL)` segfaults on `@CallState` and `@DispatchTable`

Unlike `@Struct`/`@Union` (`ForeignMemoryGenerator:260`) and flexible structs
(`FlexibleArrayGenerator:64,78,94`), neither `@CallState` nor `@DispatchTable`
checks for `MemorySegment.NULL` in `reinterpret$F`. For `@DispatchTable`, the
constructor immediately dereferences address `0x0` to load function pointers,
crashing the JVM with SIGSEGV; `@CallState` segfaults on first access.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/CallStateProcessor.java:160-164`
- `processor/src/main/java/org/alveolo/ffm/processor/DispatchTableProcessor.java:115-119`

Recommendation:

Return `null` when `memorySegment.equals(MemorySegment.NULL)`, matching
`@Struct` and `@Union`.

### P2: `@WString` and `@CFString` are silently ignored on non-`String` struct/union/record/indexed fields

`validateFields` checks `field.isString()` without rejecting `@WString` or
`@CFString` when placed on non-`String` fields (e.g.,
`@Struct interface Bad { @WString int x(); }` or indexed primitive fields).
These declarations compile with zero diagnostics and silently emit plain
primitive fields.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAnalyzer.java:502-575`

Recommendation:

Reject `@WString` and `@CFString` on any non-`String` field (including indexed
and record fields) with a diagnostic on the offending declaration.

### P2: `@Callback` does not reject unsupported AFFM annotations or `throws`, and still writes files on invalid names

1. `CallbackProcessor` only checks 5 `CARRIER_ANNOTATIONS` on `TypeMirror`;
   `@WString`, `@CFString`, `@Release`, `@Require`, `@Value`, `@Sequence`,
   `@In`, `@Out`, and `@Flexible` all compile silently with no diagnostic.
2. Checked `throws` clauses on the callback method are not rejected, even
   though an exception escaping an FFM upcall stub aborts the JVM.
3. Errors in `validateSimpleClassName`, `validateGeneratedClassName`, or
   `validateTopLevelType` do not prevent calling `write()`, which passes
   invalid class names to `Filer.createSourceFile`.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/CallbackProcessor.java:38-43`
- `processor/src/main/java/org/alveolo/ffm/processor/CallbackProcessor.java:73-128`

Recommendation:

Reject unsupported AFFM annotations (including element-level parameter/method
annotations) and `method.getThrownTypes()`, and skip `write()` when type-level
name/top-level validation fails.

### P2: `TypeGenerator.typeName()` emits binary `$` names for nested types and leaks foreign `TYPE_USE` annotations

1. `elements.getBinaryName` emits `pkg.Outer$Inner` instead of
   `pkg.Outer.Inner`, causing secondary compile errors in generated
   placeholder/accessor signatures.
2. `typeName()` on `ArrayType` and `ExecutableGenerator.returnTypeName()` fall
   back to `toString()`, leaking arbitrary `TYPE_USE` annotations (including
   package-private annotations from other packages) into generated signatures.
3. Placeholder methods for generic interface methods omit `<T>` type
   parameters.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:165-168`
- `processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:366-391`

Recommendation:

Use qualified canonical names (`TypeElement.getQualifiedName()`) instead of
binary names, strip non-AFFM type-use annotations when formatting generated
signatures, and emit method type parameters on placeholders.

### P2: Missing collision detection between fluent field setters and `@Virtual`/`@Symbol` methods

A struct declaring `int x(); @Virtual(0) void x(int value);` passes validation
because the two methods belong to different families, then emits two
conflicting `x(int)` methods in the generated class without a processor
diagnostic. The same applies to `@Symbol` methods and 2-argument fluent setters
for indexed fields.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/StructInterfaceModel.java:177-266`
- `processor/src/main/java/org/alveolo/ffm/processor/ObjectMethodsGenerator.java:91-117`

Recommendation:

Detect signature collisions between generated fluent setters and
`@Virtual`/`@Symbol` methods and report an error on the conflicting method.

### P2: Inherited fluent setters break compilation on invalid base fields and duplicate base diagnostics

When generating `DerivedF` extending `Base`, `ForeignMemoryGenerator` re-runs
`analyzer.validateFields(model.baseStruct(), baseFields, null)`, duplicating
every diagnostic from `Base`. In addition, `writeInheritedFluentSetters` checks
`analyzer.needsAllocatorWrite(field)` without checking
`fieldAccessorsShouldThrow`, emitting a 2-arg `@Override` setter that fails to
override `BaseF`'s 1-arg throwing placeholder.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryGenerator.java:70-74`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAccessorGenerator.java:53-81`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAccessorGenerator.java:502-507`

Recommendation:

Avoid re-reporting base-struct errors when generating derived structs, and match
the exact base setter signature (including throwing placeholders) in
`writeInheritedFluentSetters`.

### P2: Invalid overloads of non-`Buffer` struct fields are omitted from `unsupportedMethods`

In `ForeignMemoryAnalyzer`, invalid extra overloads of a field name are only
added to `unsupportedMethods` when `if (bufferField)`. For non-`Buffer` fields
(e.g., `int x(); void x(int a, int b);`), the invalid method is never stubbed,
leaving the generated class uncompilable.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAnalyzer.java:222-233`

Recommendation:

Stub all rejected overloads consistently (avoiding duplicates with generated
fluent setters) so unaffected members remain compilable.

### P2: `GeneratedTypeRegistry` cross-package simple-name hijacking and ambiguity

1. Resolving an unresolved simple name via `unique(bySimpleName.get(...))` can
   bind an unimported typo in package `b` to a same-named (even package-private)
   generated type in package `a`.
2. If packages `a` and `c` both define `FooF` and package `b` imports `a.FooF`,
   `unique(...)` returns `null`, causing a false `Type is not supported: FooF`.
3. `@DispatchTable` generated classes are not registered in
   `GeneratedTypeRegistry`, creating an asymmetry between precompiled and
   same-round dispatch tables.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/GeneratedTypeRegistry.java:46-95`

Recommendation:

Scope simple-name fallback to the use-site package (and imports if available),
and index `@DispatchTable` generated types alongside structs/unions/call-states.

### P2: `validateUserIdentifiers` skips inherited members on `@CallState`, `@Callback`, and `@Union`

Unlike `@ForeignInterface`, `@DispatchTable`, and `@Struct`, these three
processors pass only `getEnclosedElements()` instead of all members, allowing
inherited `$F`/`$f` identifiers through.

Evidence:

- `processor/src/main/java/org/alveolo/ffm/processor/CallStateProcessor.java:72`
- `processor/src/main/java/org/alveolo/ffm/processor/CallbackProcessor.java:76`
- `processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryProcessor.java:131`

Recommendation:

Inspect inherited members consistently across all processors.

### P3: Additional processor validation and code-hygiene items

- `TypeGenerator.hasTypeUseAnnotation` (`TypeGenerator.java:528-539`) recurses
  into `ArrayType.getComponentType()`, conflating element annotations with array
  annotations (e.g., `@Value Point @Address []` falsely triggers a pass-mode
  conflict).
- `ProcessorUtils.validateSimpleClassName` (`ProcessorUtils.java:113-121`)
  accepts restricted type keywords (`record`, `var`, `yield`, `sealed`,
  `permits`), generating uncompilable class declarations.
- Invalid record array components (`ForeignMemoryAnalyzer.java:252-260, 528-532`)
  emit a duplicate secondary `Type is not supported` diagnostic.
- Cross-annotation leaks: `@DispatchTable` ignores `@Symbol`/`@Virtual`;
  `@ForeignInterface` ignores `@Slot`/`@Virtual`; `validateLibrary`
  (`ForeignInterfaceProcessor.java:174`) uses `isEmpty()` instead of `isBlank()`,
  accepting `@Library(" ")`.
- `ExecutableGenerator.returnTypeName()` (`ExecutableGenerator.java:378-379`)
  emits `@Release`/`@Require` on generated `LibF` signatures for released
  record returns.
- `BindingAnnotations.java:141-144` performs an unchecked `(TypeElement)` cast
  on superinterface elements and does not descend into `WildcardType` bounds.
- `ForeignMemoryGenerator.java:118-123` records `foreignMemoryClassName()`
  instead of `layoutSpecification()` in `LayoutCycleValidator` and runs cycle
  validation on every non-over round instead of once at `processingOver()`.
- Dead/unused code: 3 unused constructors in `ExecutableGenerator.java:69-92`;
  3 unused methods in `ObjectMethodsGenerator.java:48-50, 67-69, 86-89`; dead
  branches in `ForeignMemoryAccessorGenerator.java:178-179`,
  `ForeignMemoryAnalyzer.java:536-540`, `TypeGenerator.java:485`; redundant
  `x == null` checks inside `asSlice(...)` in shared allocation plans
  (`ExecutableGenerator.java:670-679`, `VariableGenerator.java:238-255`).

---

## Findings — Core, Build, CI, and Docs

### P1: `ForeignUtils.jarDirectory` crashes `<clinit>` on non-`file:` `CodeSource` URIs and null locations

`source.getLocation()` is nullable (`NullPointerException`), and `Path.of(URI)`
throws `FileSystemNotFoundException` for non-`file:` URIs (Spring Boot fat JARs
`jar:file:...!/`, OSGi `bundle://`, `https://`, custom classloaders), which is
not caught by `catch (IllegalArgumentException | URISyntaxException)`. Any
`@ForeignInterface` using `@Library` fails with `ExceptionInInitializerError`
in those environments.

Evidence:

- `core/src/main/java/org/alveolo/ffm/ForeignUtils.java:165-178`

Recommendation:

Return `null` when `location == null` or `!"file".equals(location.getProtocol())`
and catch `FileSystemNotFoundException` as well.

### P1: `@Slot` has `RetentionPolicy.SOURCE`, breaking `@DispatchTable` inheritance across JARs

`README.md` documents that `@DispatchTable` abstract methods may be inherited
from parent interfaces, and `DispatchTableProcessor` reads `@Slot` on inherited
methods. Because `@Slot` uses `@Retention(SOURCE)` (unlike `@Virtual`,
`@Symbol`, and `@Fields`, which use `CLASS`), `javac` strips `@Slot` from
compiled `.class` files, breaking cross-module/JAR inheritance. `@DispatchTable`
and `@Callback` also use `SOURCE` retention.

Evidence:

- `core/src/main/java/org/alveolo/ffm/Slot.java:13`
- `core/src/main/java/org/alveolo/ffm/DispatchTable.java:17`
- `core/src/main/java/org/alveolo/ffm/Callback.java:22`
- `README.md:446-447`

Recommendation:

Change `@Slot` (and consider `@DispatchTable` and `@Callback`) to
`@Retention(CLASS)`.

### P1: `benchmark` module is not excluded from Maven Central deployment and shades over the main artifact

`benchmark/pom.xml` sets neither `maven-deploy-plugin` `<skip>true</skip>` nor
`central-publishing-maven-plugin` `<skipPublishing>true</skipPublishing>`. Its
`maven-shade-plugin` execution runs in `package` with default
`shadedArtifactAttached=false` and `createDependencyReducedPom=true`, replacing
the main artifact with an uber-jar bundling JMH, JNA, JNR, and
`alveolo-ffm-core`, and writing `benchmark/dependency-reduced-pom.xml` in the
source tree.

Evidence:

- `benchmark/pom.xml:84-116`
- `parent/pom.xml:138-212`
- `benchmark/dependency-reduced-pom.xml`

Recommendation:

Skip deployment/publishing in `benchmark/pom.xml` and set
`<createDependencyReducedPom>false</createDependencyReducedPom>` (or write it
under `${project.build.directory}`).

### P1: CI runs only on `ubuntu-latest`, leaving macOS and Windows code paths untested

No `macos-latest` or `windows-latest` jobs exist in CI. Consequently:

- `org.alveolo.ffm.macos` (`CFSupport`, `CFString`, `CoreFoundationTest`,
  `Kind.FRAMEWORK`) is skipped in CI.
- Windows LLP64 (`SLONG`/`ULONG` 32-bit narrowing) and 16-bit `WCHAR_T` paths
  in `NativeType` are never exercised in CI.
- `@WString` has no runtime/native tests (only compile-time codegen tests).
- `ForeignUtils` has no unit tests in `core/src/test`.

Evidence:

- `.github/workflows/maven.yml:12, 23`
- `core/src/test/java/org/alveolo/ffm/NativeTypeTest.java`

Recommendation:

Add `macos-latest` and `windows-latest` to the CI matrix, add unit tests for
`ForeignUtils` and `NativeType` (`intToCharExact`/`charToInt`), and add native
runtime tests for `@WString`.

### P2: macOS dyld shared cache handling in `ForeignUtils`

1. `loadPath` (`ForeignUtils.java:107-111`) calls
   `SymbolLookup.libraryLookup(Path, Arena)`, which fails on modern macOS for
   dyld shared cache libraries such as `/usr/lib/libSystem.B.dylib` because
   those paths do not exist on disk (`SymbolLookup.libraryLookup(String, Arena)`
   succeeds via `dlopen`).
2. `loadMacFramework` (`ForeignUtils.java:180-187`) passes an absolute
   `/System/Library/Frameworks/...` path through `loadName` → `loadRelocatable`,
   performing pointless probes against `affm.library.path` and `jarDirectory`
   and attaching spurious suppressed exceptions on failure. It also cannot load
   third-party frameworks from `/Library/Frameworks/`.

Evidence:

- `core/src/main/java/org/alveolo/ffm/ForeignUtils.java:107-149, 180-187`

Recommendation:

Load macOS system frameworks directly via `loadNameDirect` (optionally falling
back to `/Library/Frameworks/`), and handle macOS dyld shared cache paths in
`loadPath`.

### P2: 12 Javadoc syntax errors (`@see [...]`) masked by `<doclint>none</doclint>`

Eight public annotations (`Address`, `Value`, `Fields`, `Struct`, `Union`,
`DispatchTable`, `In`, `Out`) use `/// @see [Type]`. Under JEP 467 Markdown
Javadoc, block tags like `@see` take standard Javadoc references (`@see Struct`),
not Markdown link brackets; `javac -Xdoclint:all` reports 12 errors currently
hidden by `<doclint>none</doclint>`.

Evidence:

- `parent/pom.xml:177`
- `core/src/main/java/org/alveolo/ffm/{Address,Value,Fields,Struct,Union,DispatchTable,In,Out}.java`

Recommendation:

Replace `@see [Type]` with `@see Type` and re-enable doclint (e.g.,
`all,-missing`).

### P2: `NativeType` exposes internal adapter helpers as `public static`

`longToUnsignedIntExact`, `intToCharExact`, and `charToInt` are `public static`
only because `methodHandle(...)` uses `MethodHandles.publicLookup()` instead of
`MethodHandles.lookup()`.

Evidence:

- `core/src/main/java/org/alveolo/ffm/NativeType.java:212-226, 260-267`

Recommendation:

Switch to `MethodHandles.lookup()` and make those helpers package-private (or
`private`).

### P2: `CFSupport.fromCFString` does not reject negative `CFStringGetLength`

`Math.toIntExact(CFStringGetLength(value))` accepts negative lengths, causing a
confusing `IndexOutOfBoundsException` on `buffer.asSlice(0, rangeSize)` (or
negative allocation) instead of an explicit validation error.

Evidence:

- `core/src/main/java/org/alveolo/ffm/macos/CFSupport.java:41-56`

Recommendation:

Validate `length >= 0` before allocating.

### P2: Unchecked `long` arithmetic in `ForeignUtils.structPad` / `unionPad`, and `os()` fallback

- `structPad` and `unionPad` (`ForeignUtils.java:218-273`) do not use
  `Math.addExact` (unlike layout calculations elsewhere); also minor typos
  `byteAligment`/`maxAligment`.
- `ForeignUtils.os()` (`ForeignUtils.java:205-215`) silently classifies all
  non-macOS/non-Windows platforms (FreeBSD, Solaris, AIX, etc.) as `LINUX`.
- `Library.Override` (`Library.java:29-35`, `ForeignUtils.java:83, 189-195`)
  lacks `version()` and drops `version` on overrides; Windows `libraryName`
  silently ignores `version` without documentation.

### P2: `NativeSharedLibraryTest.run` can deadlock on verbose compiler output

`process.waitFor(30, SECONDS)` is called before draining
`process.getInputStream()`, which can deadlock if compiler diagnostics exceed
the OS pipe buffer.

Evidence:

- `benchmark/src/test/java/org/alveolo/ffm/benchmark/nativecall/NativeSharedLibraryTest.java:924-938`

Recommendation:

Drain stdout/stderr concurrently (or via a background thread / temporary file)
before `waitFor`.

### P3: Documentation, POM, and API polish

- **Dead link in `README.md:1192`**: points to
  `docs/benchmarks/cfstring-2026-09-13/README.md`, which does not exist.
- **`README.md:149-156`**: `module-info` snippet omits
  `exports org.alveolo.ffm.windows;`.
- **`README.md:1112`**: marker-annotation composition list omits `@WString`.
- **`TODO.md` and `docs/reviews/REVIEW-2026-09-05-CODEX-60AH-leftovers.md`**:
  still list already-implemented features (`@SLong`/`@SizeT`, `@Callback`,
  `@Flexible`) and the old `FM` suffix (`TODO.md:12`).
- **`@WString` lone UTF-16 surrogates** (`WString.java:10-16`,
  `VariableGenerator.java:270-297`): `StandardCharsets.UTF_16LE` silently
  replaces unpaired UTF-16 surrogates (legal in Win32/NTFS paths) with `'?'` /
  `U+FFFD`, unlike `CFSupport`'s raw `JAVA_CHAR` copy. Document or align.
- **`core/pom.xml:15-16`**: missing `<description>`, inheriting the APT
  processor description from `parent/pom.xml`.
- **Annotation defaults**: `@Slot.value()` defaults to `-1` while
  `@Virtual.value()` has no default; `core/src/main/java/module-info.java:2-4`
  uses 4-space indentation instead of 2 spaces.
- **`benchmark/.factorypath`**: tracked IDE file with hardcoded JMH/transitive
  JAR versions and without `alveolo-ffm-processor`; workflow action versions
  drift between `maven.yml` and `release.yml`.

## Test Coverage Gaps

- `ForeignUtils` unit tests (`jarDirectory` with non-`file:` URIs and null
  location, `loadRelocatable`, `callStateName`, `structPad`, `unionPad`).
- Runtime/native tests for `@WString` and Windows `NativeType` narrowing paths
  (`intToCharExact`, `charToInt`).
- Processor error-recovery and negative tests for:
  - `TypeVariable` and generic methods (`<T>`), nested types (`Outer.Inner`),
    unresolved annotation values (`@Require`, `@Struct(symbols=...)`, `@Sequence`).
  - `@Flexible("count")` with invalid canonical scalar on `byte`/`short`/`char`.
  - `@Release` referencing a method with compile errors.
  - `@Virtual` bridge with qualified NIO Buffer / record-array parameters
    carrying `@In`, `@Out`, `@Sequence`, `@Value`.
  - `@Union` with `@Virtual`, `@Symbol`, and interface inheritance.
  - `@WString`/`@CFString` on non-`String` fields; unsupported AFFM annotations
    and `throws` clauses on `@Callback`.
  - Fluent setter vs `@Virtual`/`@Symbol` collisions; invalid base-struct field
    inherited by a derived `@Struct`.
  - `reinterpret$F(MemorySegment.NULL)` on `@CallState` and `@DispatchTable`.
  - Inherited `@Slot` from a compiled binary superinterface; inherited `$F`/`$f`
    identifiers on `@CallState`/`@Callback`/`@Union`; contextual keywords in
    `name = "..."`.
  - Cross-package `GeneratedTypeRegistry` resolution with unimported/duplicate
    simple names.
