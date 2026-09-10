# Review leftovers — alveolo-ffm

Remaining items from the review dated 2026-07-09.

## Remaining concerns

1. **Unknown operating systems default to Linux.** `ForeignUtils.os()` silently
   selects Linux for unrecognized systems. Explicit rejection could make
   unsupported-platform failures clearer.
2. **Ordinary C-string returns have no decoding bound.** Generated code uses
   `reinterpret(Long.MAX_VALUE).getString(0L)`, relying on the native API's
   promise of a valid terminator. Bounded decoding is a possible feature rather
   than an established binding bug. CFString conversion already uses explicit
   lengths.

## Feature proposals

- **Callbacks/upcalls**, including callback lifetime management.
- **Critical-call options** for suitable short native calls.
- **Native-memory ownership and custom releasers** for returned pointers and
  C strings.
- **Packing and alignment overrides** for native declarations that require them.
- **Enum arrays and bitfields**, extending the scalar enum-mapping proposal.
- **Caller-provided `SymbolLookup` and possible `Linker` injection.** Lookup
  support was discussed and benchmarked but remains deferred. Linker injection
  has no established practical use case.

Several proposals overlap with the
[July 2 leftovers](REVIEW-2026-07-02-CLAUDE-F5H-leftovers.md) and
[July 6 leftovers](REVIEW-2026-07-06-CODEX-INDEPENDENT-leftovers.md).
