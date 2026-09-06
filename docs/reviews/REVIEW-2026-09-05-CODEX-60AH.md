Independent review of alveolo-ffm, 5 September 2026

Reviewed commit `5baa9a2` with an initially clean working tree. I did not open previous reviews or use their findings. This review covers core runtime helpers and annotations, every processor implementation class, representative generated fixtures and test implementations, benchmarks, Maven configuration, and CI/release workflows.

The strongest part of the project is its direct use of FFM, explicit allocation model, dependency-free core, and source-equivalence tests. The principal weakness is how individually supported features combine. Different generation paths lose parameter metadata, initialization work, or lifetime requirements. These failures can survive a successful build of the project's existing tests.

✅ `mvn clean test` succeeded on Zulu Java 25, macOS arm64. Maven reported 184 tests, with 182 passing and two disabled diagnostic-printing tests. The first incremental run used stale compiled classes; a clean rebuild resolved that environmental failure. It is not included as a source defect.

✅ `mvn package -DskipTests` also succeeded, including the shaded benchmark artifact.

I also compiled small independent Java specifications and a local C library to exercise the generated bindings. All experiments and logs are under `/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit`. No production source or existing tests were changed. Linux and Windows execution, release publication, and performance measurements were outside this local verification.

The first three findings deserve priority because valid declarations produce incorrect runtime behavior.

1. **[P1] ✅ Returned records can contain views into an already-closed arena.**

   The processor treats every record returned by value as a detached snapshot and allocates its return storage in a confined call arena. However, a record component can be an inline `@Value` interface struct. Its converter constructs a wrapper over a slice of that return storage. Closing the call arena invalidates part of the returned Java object immediately.

   Reproduction: `@Struct record Outer(@Value Inner inner) {}`, where `Inner` is a memory-backed struct interface, returned by a native `make_outer()`. The native function returns an inner integer of 42. Immediately after the generated call, the inner segment reports `isAlive() == false`, and `outer.inner().value()` throws `IllegalStateException: Already closed`. Record-array copy-out uses the same snapshot assumption and needs the same transitive analysis.

   Fix direction: distinguish fully detached record shapes from records containing inline memory views. Require caller-owned allocation for the latter, or reject automatic snapshot conversion for those shapes. Apply that decision recursively to nested records and record-array copy-out. Do not make returned views depend on the temporary call arena.

   Sources: [ExecutableGenerator.java:346](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:346), [ExecutableGenerator.java:398](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:398), [ForeignMemoryAccessorGenerator.java:358](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAccessorGenerator.java:358). Runtime evidence: [record_lifetime/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/record_lifetime/run.log).

2. **[P1] ✅ Virtual methods lose `@CountedBy`, corrupting array entries outside the requested prefix.**

   The intermediate dispatch-table specification copies parameter types and names through `bridgeSignature()`. `CountedBy` is a declaration annotation targeted only at parameters, so it is omitted. The generated dispatch method consequently transfers the entire array and loses the count validation.

   Reproduction: a virtual method `void fill(@Out @CountedBy("count") int[] values, int count)`, called with `[1, 2, 3]` and count 1. The C function only writes element zero. Expected Java result `[10, 2, 3]`; actual result `[10, 0, 0]`. The wrapper copies zero-initialized temporary storage over the untouched suffix. A missing upper-bound check can also expose native code to an oversized count; the probe did not perform an out-of-bounds native write.

   Fix direction: preserve parameter declaration annotations in the bridge, especially `@CountedBy`, and verify that direct, dispatch-table, and virtual bindings enforce identical transfer contracts.

   Sources: [VariableGenerator.java:61](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:61), [ObjectMethodsGenerator.java:364](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ObjectMethodsGenerator.java:364). Runtime evidence: [virtual_count/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/virtual_count/run.log).

3. **[P1] The shared-allocation path silently passes null for non-null `CFString` arguments.**

   When at least two temporary allocations can share backing storage, `methodBody()` uses `plannedInitializers()` instead of `paramInitializers()`. The former filters on `needsLocalAllocation()`, which excludes `CFString`. The CFString local remains initialized to `MemorySegment.NULL`, and the native invocation receives it unchanged.

   Reproduction: `int present(@CFString String cf, String a, String b)`, called with three non-null strings. A C function that checks `cf != NULL` returns 0. The generated source contains no CFString conversion for this method. Native functions that require a valid CFString may fail more severely.

   Fix direction: run non-allocation argument conversions in both paths. Allocation batching should select storage without changing which arguments are initialized or released.

   Sources: [ExecutableGenerator.java:200](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:200), [ExecutableGenerator.java:540](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:540), [VariableGenerator.java:120](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/VariableGenerator.java:120). Runtime evidence: [cf_runtime/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/cf_runtime/run.log).

4. **[P2] ✅ `@Address @SizeT` generates Java that does not compile.**

   `CanonicalLayout.SIZE_T` is declared as the base `ValueLayout` type. The generated pointee reads and writes pass that field directly to `MemorySegment.get/set`, whose overloads require a carrier-specific layout such as `ValueLayout.OfLong`.

   Reproduction: `@Address @SizeT long read(@Address @SizeT long value)`. Javac rejects both the generated read and write. `SizeT` JavaDoc explicitly advertises combining it with `Address`. Ordinary by-value size_t calls pass the existing tests because their descriptors accept the base layout type.

   Fix direction: expose a typed size_t layout under the documented 64-bit restriction, or provide typed runtime access helpers. Add positive coverage for canonical scalar pointees, including record components.

   Sources: [CanonicalLayout.java:10](/Users/igor/work/alveolo/alveolo-ffm/core/src/main/java/org/alveolo/ffm/CanonicalLayout.java:10), [TypeGenerator.java:488](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:488), [TypeGenerator.java:506](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:506). Evidence: [sizeptr/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/sizeptr/compile.log).

5. **[P2] ✅ Virtual methods cannot return interface structs by value with an allocator.**

   A source declaration such as `@Virtual(0) @Value Point get(SegmentAllocator allocator)` passes the initial checks. The generated dispatch-table specification inserts `self$f` before the allocator. Its next processing round then rejects the method because the allocator is no longer the first Java parameter.

   Fix direction: put Java-only allocator parameters before the inserted native receiver in the bridge, and make the delegated call follow that order. Keep native receiver ordering separate from Java parameter ordering.

   Sources: [ObjectMethodsGenerator.java:342](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ObjectMethodsGenerator.java:342), [ObjectMethodsGenerator.java:364](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ObjectMethodsGenerator.java:364), [ExecutableGenerator.java:743](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:743). Evidence: [virtual_allocator/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/virtual_allocator/compile.log).

6. **[P2] Vtable inheritance breaks at the third struct level.**

   The model derives vtable presence from the immediate physical base's annotation rather than its effective inherited layout. For `Base` marked `@Struct(vtable=true)`, ordinary `@Struct Mid extends Base`, and `@Struct Leaf extends Mid`, `Mid` inherits the vtable but its annotation still says false. `Leaf` then loses the inherited vtable flag and rejects inherited or newly declared virtual methods.

   Fix direction: derive vtable presence from the resolved base hierarchy, not just the immediate annotation. Add a three-level inheritance fixture and an execution test with inherited and new slots.

   Source: [StructInterfaceModel.java:106](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/StructInterfaceModel.java:106). Evidence: [vtablechain/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/vtablechain/compile.log).

7. **[P2] Precompiled derived wrapper classes are not recognized as foreign memory.**

   Wrapper detection requires a declared `MemorySegment$F` field. A generated derived struct wrapper inherits that field from its base. Once compiled, the derived wrapper therefore fails detection when another binding uses it directly.

   Reproduction: compile `Header` and `Packet extends Header`, then separately compile `@ForeignInterface interface Packets { void send(PacketFM packet); }` against those class files. The processor reports `Type is not supported: PacketFM`. This breaks the documented ability to use generated wrapper types directly and makes behavior depend on whether the classes were generated in the current compilation.

   Fix direction: inspect inherited members or persist explicit wrapper metadata. Verify both fresh-source and separately compiled clients.

   Source: [TypeGenerator.java:593](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:593). Evidence: [binary_wrapper/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/binary_wrapper/compile.log).

8. **[P2] Inherited `@CountedBy` methods require undocumented parameter-name retention.**

   The annotation stores a sibling's source name, and validation compares it against `VariableElement` names. When a parent interface is compiled without `-parameters`, a later compilation sees names such as `arg0` and `arg1`, while the annotation still contains `"count"`.

   Reproduction: compile an ordinary parent interface containing `fill(@CountedBy("count") int[] values, int count)` without `-parameters`, then compile an annotated child extending it. The processor says the annotation does not name a parameter.

   Fix direction: either document and validate the `-parameters` requirement for reusable binary interfaces, with a targeted diagnostic, or persist a count-parameter index in processed metadata. An index-based public annotation would also avoid the source-name dependency, but is an API choice rather than a necessary first fix.

   Source: [ExecutableGenerator.java:853](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:853). Evidence: [binary_count/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/binary_count/compile.log).

9. **[P2] The documented named-module setup does not compile generated classes.**

   README tells applications to add `requires org.alveolo.ffm;`. Every companion also references `javax.annotation.processing.Generated`, which belongs to `java.compiler`. That module is not readable under the documented setup.

   Reproduction: compile `module audit.client { requires org.alveolo.ffm; }` with a single annotated struct. Javac reports that `javax.annotation.processing` is not visible from the client module.

   Fix direction: document `requires static java.compiler;` if retaining this annotation, or omit it where unavailable. Add a minimal JPMS consumer test that follows the published instructions. Native-access guidance should also cover restricted calls performed by core helpers, not only generated client classes.

   Sources: [ForeignMemoryGenerator.java:97](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryGenerator.java:97), [README.md:145](/Users/igor/work/alveolo/alveolo-ffm/README.md:145). Evidence: [modules/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/modules/compile.log).

10. **[P2] A legal parameter named `result` collides with generated return-allocation names.**

    The allocation plan uses parameter names as allocation identities and also uses the literal identity `result` for record return storage. The reserved-suffix validation does not prohibit the ordinary name `result`, nor should it need to.

    Reproduction: `Pair call(String result)`, where `Pair` is a struct record. Javac reports duplicate `result$allocationOffset$f` and `result$allocation$f` locals.

    Fix direction: give return storage an identity distinct from all parameter allocations, preferably using a parameter index or explicit allocation kind. Keep ordinary user names legal.

    Source: [ExecutableGenerator.java:606](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:606). Evidence: [resultname/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/resultname/compile.log).

11. **[P2] CFString conversion truncates embedded NUL characters.**

    Both directions go through NUL-terminated UTF-8 strings. CFString is a length-bearing string type, so this loses content representable in both Java and CoreFoundation.

    Reproduction: round-trip the three-character Java string `"a\0b"` through `toCFString()` and `toJavaString()`. The result has length 1.

    Fix direction: use length-based conversion in both directions and test embedded NUL, non-ASCII text, and supplementary characters. C `char*` string termination semantics should not silently define CFString semantics.

    Source: [CFStringSupport.java:60](/Users/igor/work/alveolo/alveolo-ffm/core/src/main/java/org/alveolo/ffm/macos/CFStringSupport.java:60). Evidence: [cf_nul/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/cf_nul/run.log).

There are five smaller correctness and consistency issues worth addressing alongside those fixes.

- **Scalar and indexed pointers disagree about null.** In a zeroed struct, an indexed child pointer getter returns Java null, while a scalar child pointer getter returns a wrapper around address zero. The scalar setter also throws on Java null, while indexed setters accept it. This is a surprising difference for equivalent pointer fields. Choose and document a consistent policy; avoid manufacturing a usable-looking wrapper around a null address. Sources: [ForeignMemoryAccessorGenerator.java:598](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryAccessorGenerator.java:598), [IndexedFieldGenerator.java:110](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/IndexedFieldGenerator.java:110). Evidence: [nullscalar/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/nullscalar/run.log). The probe did not dereference address zero.
- **Impossible recursive inline layouts compile and fail during class initialization.** `@Struct record Recursive(Recursive child) {}` is accepted, but accessing its generated layout throws `ExceptionInInitializerError` caused by a null self-reference. Reject cycles along by-value layout edges while continuing to allow cycles broken by address fields. Sources: [TypeGenerator.java:190](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/TypeGenerator.java:190), [ForeignMemoryGenerator.java:147](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ForeignMemoryGenerator.java:147). Evidence: [rec_runtime/run.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/rec_runtime/run.log).
- **Call-state validation ignores inherited abstract members.** A call-state interface that declares one valid accessor but inherits a second abstract method passes processor validation, then fails in generated Java. Either reject unsupported inheritance explicitly or validate the complete abstract method set. Source: [CallStateProcessor.java:105](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/CallStateProcessor.java:105). Evidence: [callstateinherit/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/callstateinherit/compile.log).
- **Java string-literal handling is inconsistent.** Symbol names are inserted directly between quotes instead of using the shared quoting function. `@Symbol("has\"quote")` produces invalid Java. The shared `quote()` itself only escapes backslashes and quotes, leaving line breaks and other special characters unhandled. Use one complete literal encoder across symbols, library specifications, and call-state overrides. Sources: [ExecutableGenerator.java:128](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ExecutableGenerator.java:128), [ProcessorUtils.java:174](/Users/igor/work/alveolo/alveolo-ffm/processor/src/main/java/org/alveolo/ffm/processor/ProcessorUtils.java:174). Evidence: [symbolquote/compile.log](/Users/igor/work/alveolo/alveolo-ffm/tmp/independent-audit/symbolquote/compile.log).

- **The allocator documentation overstates the requirement.** README says a struct-by-value return requires an explicit `SegmentAllocator`, but record returns use an internal arena and reject an explicit allocator. Narrow this wording to memory-backed interface returns, then document any transitive lifetime restriction introduced by the fix for finding 1. Source: [README.md:635](/Users/igor/work/alveolo/alveolo-ffm/README.md:635).

For maintainability, I would make a few targeted changes before adding more conversion modes.

- **Unify argument preparation around one conversion decision.** `VariableGenerator` has separate planned and direct initializers for strings, primitive addresses, arrays, record arrays, and buffers. `ExecutableGenerator` separately assembles allocation planning, descriptors, invocation arguments, cleanup, and adaptation. The missing CFString initialization demonstrates actual divergence. A small immutable per-parameter description of layout, conversion, transfer, and allocation needs would let the storage strategy vary without duplicating semantics. Keep the generated source direct and readable.
- **Preserve method metadata once.** Direct calls, dispatch tables, and virtual bridges should consume the same resolved parameter description. Include declaration annotations, Java-only arguments, native positions, and inherited information. This addresses the counted-prefix and allocator-order bugs without adding special cases to each writer.
- **Consolidate hierarchy analysis.** Several classes independently collect abstract methods and inspect declared members. The differences matter: call states miss inherited methods, wrapper detection misses inherited fields, and vtables lose effective ancestry. Share only the mechanics that are truly common and keep struct field-order rules explicit.
- **Remove unused code and stale comments.** `ObjectMethodsGenerator.objectMethods(TypeElement)` has no caller. `ForeignUtils.loadPlatformLibrary()` takes an unused `defaultLookup`. `TypeGenerator.layout()` still has a TODO describing nested structures/reference arrays too broadly, despite existing support for several such forms. Processor module `requires java.logging` appears unused. These are small cleanups, not reasons for a broad rewrite.
- **Normalize scalar versus indexed field generation.** Pointer null handling and nested record conversion are repeated in the scalar and indexed writers. Share their conversion expressions where practical, while retaining separate field-layout and indexing logic.
- **Avoid repeated analysis inside generation.** Allocation eligibility and allocation lists are recomputed, and record conversion requirements repeatedly traverse component graphs. Compute an immutable result once per method or type. This is a simplification opportunity; I did not measure processor performance.
- **Tighten immutable helper contracts.** `ForeignUtils.LibrarySpec`, `LibraryOverride`, and `CallStateOverride` expose mutable arrays through record components. Generated code uses them transiently, so this is not one of the demonstrated runtime failures. If these remain public value objects, defensively copy array inputs and outputs or choose immutable collections.

The tests provide a useful foundation, but the gaps are concentrated in combinations and packaging boundaries.

| Area | Useful additional verification |
| --- | --- |
| Temporary allocation | The same binding shapes with one allocation and with enough allocations to activate batching; mixed CFString and ordinary arguments |
| Native return lifetime | Nested record/interface combinations, record-array copy-out, and access after the call returns |
| Virtual calls | Counted arrays, output-only arrays, allocator-backed returns, call state, and three-level inheritance |
| Separate compilation | Binary parents with and without parameter names, generated derived wrappers, and annotated inherited method signatures |
| JPMS | A tiny application built exactly as README instructs, including generated annotations and native access |
| Canonical scalars | Positive scalar, pointee, record-field, and wrapper-field coverage for each supported annotation |
| Platforms | Linux, macOS, and Windows native execution; current CI runs only Ubuntu |
| ABI layouts | Compare Java size, alignment, and offsets to a compiled C probe for structs, unions, nested arrays, and canonical scalars |

Keep complete generated-source equivalence checks where the test is about generation, and add runtime checks for behavior those source fixtures cannot establish. Several current positive cases only assert compilation success. Existing native tests live in the benchmark module; a dedicated integration-test module would make their role clearer and let correctness runs avoid JMH/JNA/JNR dependencies when those comparisons are irrelevant.

For builds and benchmarks, centralize JUnit and Surefire versions in the parent so the core and processor modules do not drift. Add macOS and Windows jobs before expanding platform-sensitive scalar support. The release workflow publishes artifacts before pushing the release commit and tag, so a push failure can leave an already-published release without matching remote source refs; document recovery and validate the intended branch state before publishing. I did not run the release workflow. Raw allocation benchmarks return constants and do not consume the allocated segments, while field-access comparisons use different access styles across AFFM, JNA, and JNR. Treat them as measurements of those exact operations. Add realistic mixed-call cases and consume relevant outputs before using the numbers to justify allocation complexity. I did not run JMH or infer performance improvements from source size.

The following features would be useful after the runtime and compilation defects are fixed. Their ranking follows gaps visible in this repository, not a survey of other libraries.

| Priority | Feature | Concrete value and initial scope |
| --- | --- | --- |
| 1 | Injectable symbol lookup and optional symbols | Allow a binding instance to use a caller-provided `SymbolLookup`. This supports tests, multiple library versions, and caller-controlled library arenas. Add explicit availability checks for optional entry points so one missing symbol need not disable an entire binding class. |
| 2 | Generated callbacks and upcall stubs | Complete the native interoperation path for event handlers, comparators, and visitor APIs. Start with fixed signatures, explicit arena ownership, and a defined exception policy at the native boundary. |
| 3 | Canonical scalar arrays | Support `@SLong`, `@ULong`, `@SizeT`, and `@WCharT` in inline arrays and call arrays. Reuse scalar conversion rules and establish Windows coverage first. This removes manual C-long array marshalling. |
| 4 | Explicit string contracts | Add nullable C-string parameters, bounded pointer-return decoding, and encoding choices where native APIs require them. Keep raw MemorySegment bindings available when lifetime or termination is external. |
| 5 | Native layout verification | Generate or expose enough metadata for a small C-side ABI conformance test to compare `sizeof`, alignment, and field offsets. This gives binding authors a concrete way to check a declaration before production use. |
| 6 | Flexible array members and controlled packing | Start with one trailing flexible member whose count comes from the caller. Add explicit packing/alignment only for real target ABIs and verify against C; avoid attempting every layout extension at once. |

I would first fix the three runtime failures and add regression cases that cross generation paths. Next, address the compiler and binary-consumer failures, then simplify shared preparation and metadata handling. Injectable lookup and callbacks are the most useful feature additions once those foundations are reliable.
