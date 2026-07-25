# Efficient Arena Allocation

Status: design decision, 2026-07-19.

## Decision

Keep `Arena.ofConfined()` as the owner of call-scoped native memory. When a
generated method needs one native allocation, allocate it directly from the
arena. When it needs two or more simultaneously live allocations whose sizes
and alignments are known, allocate one backing segment and serve the logical
allocations from a `SegmentAllocator.slicingAllocator(...)`.

The same slicing allocator should serve argument materialization and the
allocator parameter used by the linker for a struct returned by value. This
reduces native allocation and cleanup work without changing ownership: closing
the confined arena still invalidates every slice and releases the backing
region.

This note records the intended generation strategy. It does not introduce
cross-call pooling, thread-local storage, new public APIs, or a generator change
by itself.

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

These details are properties of the observed OpenJDK implementation and
HotSpot optimizer, not requirements of the FFM API. Other JDK releases or JVMs
may use different allocation and escape-analysis strategies. The generated
shape remains useful because it expresses the intended allocation strategy
explicitly.

## Generated Shape

A record passed to and returned from a native function currently has the
equivalent allocation shape:

```java
try (var arena = Arena.ofConfined()) {
  return ResultFM.fromMemorySegment$F((MemorySegment) handle.invokeExact(
      (SegmentAllocator) arena,
      ArgumentFM.toMemorySegment$F(arena, argument)));
}
```

Both the argument conversion and the linker return allocate from `arena`.
Where both layouts are statically known, generate the equivalent of:

```java
try (var arena = Arena.ofConfined()) {
  var allocator = SegmentAllocator.slicingAllocator(
      arena.allocate(requiredSize, requiredAlignment));

  return ResultFM.fromMemorySegment$F((MemorySegment) handle.invokeExact(
      (SegmentAllocator) allocator,
      ArgumentFM.toMemorySegment$F(allocator, argument)));
}
```

Java evaluates invocation arguments from left to right. Argument conversions
therefore consume their slices before the linker requests the return slice.
All slices remain live through the downcall and share the backing arena's
lifetime.

Generated memory wrappers already accept `SegmentAllocator`, so this strategy
composes with existing constructors and `allocate$F(...)` and
`toMemorySegment$F(...)` helpers. It should not require an arena-specific
overload.

## Size and Alignment

Backing capacity must include padding between logical allocations. For each
allocation request in evaluation order:

1. Align the current offset upward to the requested alignment.
2. Add the requested byte size using checked arithmetic.
3. Use the greatest requested alignment for the backing allocation.

The generated capacity calculation must apply the same rules as the slicing
allocator. Simply summing layout sizes is incorrect when adjacent layouts have
different alignments. Static layout sizes should produce static generated
expressions; no general-purpose runtime allocation planner is needed.

Use direct arena allocation instead when the complete request sequence cannot
be bounded concisely before the call. Examples include dynamically sized
strings, variable element counts, and converters that can make hidden
data-dependent allocations. A smaller predictable optimization is preferable
to speculative sizing or over-allocation.

## Return Storage

A downcall returning a struct by value accepts a `SegmentAllocator`, not a
specific `MemorySegment`. That does not prevent preallocated return storage:

- Pass the shared slicing allocator when arguments and the return value occupy
  consecutive parts of one call-scoped backing segment.
- Wrap a dedicated result segment with
  `SegmentAllocator.prefixAllocator(resultSegment)` when exactly one return
  allocation should reuse that segment.

Do not use one prefix allocator for simultaneously live arguments and a return
value: every request starts at offset zero and would overlap. Slicing is the
correct model for multiple live values.

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

Before changing generation, add full-equivalence processor tests for methods
with:

1. Two or more fixed-size record arguments.
2. A fixed-size record argument and record return.
3. Mixed sizes and alignments that require padding.
4. A caller-provided allocator and memory-backed interface return.
5. A dynamic allocation that must retain the direct-arena fallback.

Benchmark the complete generated downcalls, not only unused slice creation.
Use the short JMH configuration for quick directional checks and occasional
multi-fork, GC-profiled runs to confirm allocation behavior before relying on a
result.
