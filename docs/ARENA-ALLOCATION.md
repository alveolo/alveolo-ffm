# Efficient Arena Allocation

Status: implemented, 2026-07-25.

## Decision

Keep `Arena.ofConfined()` as the owner of call-scoped native memory. When a
generated method needs one native allocation, allocate it directly from the
arena. When it needs two or more simultaneously live allocations whose sizes
and alignments can be determined before the call, allocate one backing segment
and serve the logical allocations with direct `MemorySegment.asSlice(...)`
calls.

Argument converters write directly into their assigned slices. A
`SegmentAllocator.prefixAllocator(...)` over the dedicated result slice serves
the allocator parameter used by the linker for a struct returned by value.
This reduces native allocation and cleanup work without changing ownership:
closing the confined arena still invalidates every slice and releases the
backing region.

This implementation does not introduce cross-call pooling, thread-local
storage, or new public APIs.

## Motivation

An arena controls lifetime and access. It does not promise that repeated
`allocate(...)` calls share a native backing allocation. In the current
OpenJDK 25 implementation, each ordinary arena allocation obtains and zeroes a
separate native region and registers a separate cleanup action. Closing the
arena walks those actions and releases the regions.

A quick directional JMH run on Zulu/OpenJDK HotSpot 25.0.3 on Windows produced:

| Operation | Average time |
| --- | ---: |
| Materialized confined arena, no native allocation | `65.9 ns` |
| One 8-byte arena allocation | `130.0 ns` |
| One 64-byte arena allocation | `138.5 ns` |
| Eight 8-byte arena allocations | `1.15 us` |
| Eight 64-byte arena allocations | `1.46 us` |
| One 512-byte arena allocation followed by eight logical slices | `197.9 ns` |

The machine and short benchmark configuration were noisy, so these values are
not performance guarantees. The difference is nevertheless large enough to
show the allocation shape: for small regions, native allocation, cleanup
registration, and release dominate the payload size. GC profiling also showed
an observed HotSpot optimization boundary: the one-allocation shape created
effectively no Java-heap garbage after scalar replacement, while the
eight-allocation shape created about `544 B/op`.

The slice microbenchmark did not use the returned slices, so the compiler could
eliminate some logical slicing work. It demonstrates the benefit of
consolidating native allocations, not the complete cost of a generated foreign
call. Any generator change must also be checked with end-to-end downcall
benchmarks in which argument and return slices are actually used.

An end-to-end JMH benchmark on Zulu/OpenJDK HotSpot 25+36 on Apple M-series
macOS compared a generated `abs(IntR)` downcall which materializes one record
argument and one record return:

| Allocation shape | Average time |
| --- | ---: |
| Two confined arena allocations | `63.828 ± 0.506 ns` |
| One backing allocation with a slicing allocator | `34.791 ± 0.351 ns` |
| One backing allocation with direct slices | `33.588 ± 0.251 ns` |
| Generated direct-slice implementation | `33.392 ± 0.348 ns` |
| Hand-written direct-slice implementation | `33.329 ± 0.209 ns` |

Each main comparison used three forks and 24 measurement iterations. A shorter
GC-profiled comparison measured `64 B/op` for both consolidated variants.
Direct slicing was about 3.5% faster than the slicing allocator and the
generated implementation matched the hand-written form, so generation uses
direct slices.

These details are properties of the observed OpenJDK implementation and
HotSpot optimizer, not requirements of the FFM API. Other JDK releases or JVMs
may use different allocation and escape-analysis strategies. The generated
shape remains useful because it expresses the intended allocation strategy
explicitly.

## Generated Shape

Before this change, a record passed to and returned from a native function had
the equivalent allocation shape:

```java
try (var arena = Arena.ofConfined()) {
  return ResultFM.fromMemorySegment$F((MemorySegment) handle.invokeExact(
      (SegmentAllocator) arena,
      ArgumentFM.toMemorySegment$F(arena, argument)));
}
```

Both the argument conversion and the linker return allocated from `arena`.
When both requests can be planned before the call, generation now emits the
equivalent of:

```java
try (var arena = Arena.ofConfined()) {
  var memory = arena.allocate(requiredSize, requiredAlignment);
  var argumentMemory = memory.asSlice(argumentOffset, argumentLayout);
  ArgumentFM.toMemorySegment$F(argument, argumentMemory);
  var resultMemory = memory.asSlice(resultOffset, resultLayout);

  return ResultFM.fromMemorySegment$F((MemorySegment) handle.invokeExact(
      (SegmentAllocator) SegmentAllocator.prefixAllocator(resultMemory),
      argumentMemory));
}
```

All slices remain live through the downcall and share the backing arena's
lifetime. Record arrays are written directly into element slices of their final
array segment rather than allocating one temporary segment per element.

Generated record wrappers already provide a converter which writes into a
caller-provided segment, so this strategy does not require an arena-specific
overload.

## Size and Alignment

Backing capacity must include padding between logical allocations. For each
allocation request in evaluation order:

1. Align the current offset upward to the requested alignment.
2. Add the requested byte size using checked arithmetic.
3. Use the greatest requested alignment for the backing allocation.

The generated capacity calculation applies the same rules as a slicing
allocator. Simply summing layout sizes is incorrect when adjacent layouts have
different alignments. Generated code aligns each offset and uses checked
arithmetic for both padding and sizes.

Top-level dynamically sized strings, arrays, and heap buffers are planned:
UTF-8 bytes and element counts are obtained before allocating the backing
segment. Use the direct-arena fallback when a record converter can make hidden
data-dependent allocations, because the complete object graph cannot be
bounded concisely before the call. A smaller predictable optimization is
preferable to speculative sizing or over-allocation.

## Return Storage

A downcall returning a struct by value accepts a `SegmentAllocator`, not a
specific `MemorySegment`. That does not prevent preallocated return storage:

- Pass the shared slicing allocator when arguments and the return value occupy
  consecutive parts of one call-scoped backing segment.
- Wrap a dedicated result segment with
  `SegmentAllocator.prefixAllocator(resultSegment)` when exactly one return
  allocation should reuse that segment.

The implementation uses the second form: direct, non-overlapping argument
slices and one prefix allocator dedicated to the result slice. Do not use one
prefix allocator for simultaneously live arguments and a return value: every
request starts at offset zero and would overlap.

Memory-backed interface results continue to use caller-owned allocation because
their lifetime can extend beyond the generated method. Record results can use a
local confined arena because conversion produces a detached Java value before
the arena closes.

## Scope and Non-goals

This optimization applies only within one generated call scope. It deliberately
does not reuse native memory across calls. Cross-call or thread-local scratch
storage adds reentrancy, concurrency, retention, and callback hazards that are
not justified by the current measurements.

The optimization must preserve:

- one confined lifetime for all call-local segments;
- the original allocation order;
- each layout's size and alignment;
- non-overlap of simultaneously live values;
- caller ownership for allocator-taking public methods; and
- conversion of record results before closing the arena.

## Verification

Full-equivalence processor tests cover methods with:

1. ✅ Two or more fixed-size record arguments.
2. ✅ A fixed-size record argument and record return.
3. ✅ Mixed sizes and alignments that require padding.
4. ✅ A caller-provided allocator and memory-backed interface return.
5. ✅ Dynamically sized UTF-8 and array allocations.
6. ✅ Record arrays written into final element slices.
7. ✅ A record converter with hidden allocations that retains the direct-arena
   fallback.

Benchmark the complete generated downcalls, not only unused slice creation.
Use the short JMH configuration for quick directional checks and occasional
multi-fork, GC-profiled runs to confirm allocation behavior before relying on a
result.
