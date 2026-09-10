# Review leftovers — alveolo-ffm

Remaining items from the review dated 2026-07-06.

## Maintainability

- **Primitive-to-buffer mapping.** `TypeGenerator` maps buffer classes to
  element types, while `IndexedFieldGenerator` separately maps primitives back
  to buffer classes. Consolidate these mappings.

## Feature proposals

1. **Ownership of returned native memory.** Custom releasers for returned C
   strings and pointers. `@CFString(owned = true)` currently covers only
   CFString. Establish a practical API use case before designing broader support.
2. **Callbacks and function-pointer fields.** Generated upcall glue and typed
   function-pointer fields. Callback support overlaps with the proposal in
   [the July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md).
3. **Additional library-lookup controls.** Optional libraries, always including
   the default lookup, caller-provided `SymbolLookup`, and lazy resolution.
   Missing functions already fail independently. Caller-provided lookup was
   discussed and benchmarked; implementation remains deferred.
