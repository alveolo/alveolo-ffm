# Review leftovers — alveolo-ffm

Remaining items from the review dated 2026-09-05.

1. **Mutable arrays in runtime helper records.** `ForeignUtils.LibrarySpec`,
   `LibraryOverride`, and `CallStateOverride` expose their arrays directly.
   Immutable collections or defensive copies could tighten these contracts.
   This is optional API cleanup; generated code uses them transiently, with no
   demonstrated failure.
2. **Cross-platform CI.** CI currently runs only Ubuntu. Add macOS and Windows
   jobs to exercise platform-specific behavior, particularly canonical scalar
   widths and library loading.
3. **Canonical scalar arrays.** Support `@SLong`, `@ULong`, `@SizeT`, and
   `@WCharT` for inline array fields and call arrays, reusing scalar adaptation
   rules. These annotations currently support scalar values, not array elements.
4. **Flexible array members.** Support a trailing variable-length member in a
   native struct. Packing/alignment overrides overlap with proposals in earlier
   reviews.
5. **Other feature proposals.** Callbacks/upcall stubs, caller-provided
   `SymbolLookup`, bounded string decoding, and additional string encodings.
   Caller-provided lookup was discussed and benchmarked but remains deferred.

Overlapping proposals are recorded in the
[July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md),
[July 6 leftovers](REVIEW-2026-07-06-CODEX-INDEPENDENT-leftovers.md), and
[July 9 leftovers](REVIEW-2026-07-09-CODEX-56SH-leftovers.md).
