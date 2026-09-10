# Review leftovers — alveolo-ffm

Remaining feature proposals from the review dated 2026-07-04.

1. **C-string return ownership.** Custom release handling for strings allocated
   by native code.
2. **Pointer-to-pointer output parameters.** Convenience support for APIs such
   as `int create_item(Item **out)`, where native code writes a pointer into
   caller-provided storage. Raw `MemorySegment` bindings can already express
   this manually.
3. **Callback/upcall generation.** Generate glue for native callbacks.
4. **Packed/aligned structs.** Explicit packing and alignment options for native
   declarations that require them.

Ownership, callback, and packing proposals overlap with the
[July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md) and
[July 6 leftovers](REVIEW-2026-07-06-CODEX-INDEPENDENT-leftovers.md).
