# Review leftovers — alveolo-ffm

Remaining items from the review dated 2026-07-06.

## Feature proposals

- **Callbacks and function-pointer fields.** Generated upcall glue and typed
   function-pointer fields. Callback support overlaps with the proposal in
   [the July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md).
- **Additional library-lookup controls.** Optional libraries, always including
   the default lookup, caller-provided `SymbolLookup`, and lazy resolution.
   Missing functions already fail independently. Caller-provided lookup was
   discussed and benchmarked; implementation remains deferred.
