# Review leftovers — alveolo-ffm

Remaining feature proposals from the review dated 2026-07-04.

- **Pointer-to-pointer output parameters.** Convenience support for APIs such
   as `int create_item(Item **out)`, where native code writes a pointer into
   caller-provided storage. Raw `MemorySegment` bindings can already express
   this manually.
- **Callback/upcall generation.** Generate glue for native callbacks.
- **Packed/aligned structs.** Explicit packing and alignment options for native
   declarations that require them.

Ownership, callback, and packing proposals overlap with the
[July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md) and
[July 6 leftovers](REVIEW-2026-07-06-CODEX-INDEPENDENT-leftovers.md).
