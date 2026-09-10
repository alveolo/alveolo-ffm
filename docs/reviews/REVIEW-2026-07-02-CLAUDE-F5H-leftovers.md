# Review leftovers — alveolo-ffm

Remaining items from the review dated 2026-07-02.

## Maintainability

- **Suppressed-exception chaining in `ForeignUtils`.** Library-loading attempts
  repeat the logic for attaching the previous failure to the latest exception.
  A small shared helper could remove this duplication while preserving lookup
  order and diagnostics.

## Feature proposals

1. **Upcalls / function pointers.** Generate callback glue for comparators,
   event handlers, and other native callbacks, potentially through a
   `@Callback` functional-interface annotation.
2. **Critical calls.** Opt-in `Linker.Option.critical()` support for short native
   calls. Benchmark practical cases before adding it; eligible heap arrays and
   buffers could use `critical(true)` to avoid copying.
3. **C enum mapping.** Marshal annotated Java enums as native integer values.
4. **Packed/aligned structs.** Explicit struct packing or field-alignment
   overrides for native declarations that require them.
