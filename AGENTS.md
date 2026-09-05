This project is a wrapper around Java Foreign Function and Memory API.

## Source code

- Java 25, JMH benchmarks, Maven
- No external dependencies for core
- indentation base is two spaces
- Markdown style JavaDoc
- prefer elegant concise simple code
- design with records / immutable objects
- no stupid getters - just expose final field
- test code generation with full equivalence, not contains
- Record-based `@Struct` declarations are concise snapshots. Never support
  memory-backed struct/union interfaces, call-state wrappers, or their generated
  classes as record components, including with `@Value`, `@Address`, or through
  nested records and arrays. Use an interface for mixed layouts. Preserve the
  compile-rejection contract in `RecordStructContractTest`; do not replace it
  with allocator or lifetime machinery to accommodate these shapes.
- if you met problem unforeseen during planning and it doesn't solve in the
  most concise simple way - stop, explain the problem and ask for directions
  instead of falling back to introducing unexpected complexity.

## Experiments and file locations

For experimental code (JShell) avoid writing outside of project locations
such as /private/tmp etc. Instead write into projects internal tmp directory.

## Backwards compatibility

The project is in an early stage where changes should not care about backwards
compatibility. Some developments could totally reshape the project. Do not
attempt to maintain backwards compatibility or warn users in errors/warnings
about historical inconsistencies. Pretend the current state is the only one
ever existed until project version is less than 0.9.

## Review Markdown files

- use Unicode White Heavy Check Mark symbol for completed tasks: ✅
