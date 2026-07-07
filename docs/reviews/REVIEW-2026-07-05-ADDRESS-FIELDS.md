# Focused Review: `@Address` Field Handling

This review focuses on `@Address` handling across memory layout generation,
record conversion, generated memory accessors, and native-call parameter
conversion.

## Summary

The generator had several places where `ADDRESS` layout classification was
accepted without a matching conversion/lifetime rule. The largest issue was
allocator ownership for records that transitively contain `@Address` record
fields. The remaining issues were validation gaps: string memory fields,
arbitrary non-FFM `@Address` classes, and primitive `@Address` annotations.

Primitive `@Address` handling is now intentionally supported where it follows
the same lifetime model as record `@Address`: foreign-call parameters use
call-scoped temporary storage, foreign-call returns dereference the returned
address, and record-owned fields require a caller allocator. Memory-backed
interface fields still reject `@Address` primitives because a generated wrapper
setter would otherwise store a pointer to hidden temporary storage.

Each issue below is covered by a processor test that encodes the desired
behavior and guards the corresponding fix.

## P1: ✅ Allocator need is not transitive through nested record-by-value fields

`ForeignMemoryProcessor.writeRecordConverters` currently detects allocator
need only for direct record components whose own pass mode is address. A record
component passed by value can itself contain an `@Address` record field and
therefore still require allocator-scoped conversion.

Example:

```java
@Struct
record Pair(int left, int right) {}

@Struct
record PairBox(@Address Pair pair) {}

@Struct
record Outer(PairBox box) {}
```

`PairBoxFM` correctly has only allocator-scoped conversion, but `OuterFM`
attempts to call the removed no-allocator conversion for its `PairBox` value
field.

Test:

- `ForeignMemoryProcessorTest.generatesTransitiveRecordAddressValueField`

Expected fix direction:

- Add a recursive "record conversion requires allocator" query.
- Use it for record converter branch selection and for nested record value
  setters on both generated record helpers and memory-backed wrappers.

## P2: ✅ String memory fields use address layout but have no string conversion

`TypeGenerator` maps `String` to `ValueLayout.ADDRESS`, but generated memory
field accessors use generic VarHandle get/set code. That makes getters cast a
native address segment to `String`, and setters attempt to store a Java
`String` into an address VarHandle.

Example:

```java
@Struct
record BadStruct(String value) {}
```

Test:

- `ForeignMemoryProcessorTest.failsStringFieldOnMemoryStruct`

Expected fix direction:

- Reject `String` fields for `@Struct` / `@Union` memory types until explicit
  allocator/string field semantics are designed.

## P2: ✅ Arbitrary non-FFM classes are under-validated

An arbitrary class such as `Object` is not a supported FFI type. An explicit
`@Address` annotation must not turn that unsupported base type into
`ValueLayout.ADDRESS`; the annotation is secondary to the unsupported Java type.

Example:

```java
@ForeignInterface
interface Lib {
  void f(Object value);
  void g(@Address Object value);
}
```

Tests:

- `ForeignInterfaceProcessorTest.failsNonForeignMemoryClassParameter`
- `ForeignInterfaceProcessorTest.failsAddressOnUnsupportedClassParameter`

Expected fix direction:

- Emit an unsupported-type processor diagnostic for arbitrary Java classes
  instead of allowing `@Address` to select pointer layout and later generating
  code that relies on a nonexistent `.ms` member.

## P3: ✅ Primitive `@Address` needs explicit lifetime semantics

`TypeGenerator.isAddress()` returned `false` immediately for primitive types,
so an annotation such as `@Address long` was treated as a plain scalar value
without any diagnostic. Treating it as a pointer is useful, but only in the
same scoped places where `@Address record` is valid.

Example:

```java
@ForeignInterface
interface Lib {
  int f(@Address int value);
  @Address int g();
}

@Struct
record IntBox(@Address int value) {}

@Struct
interface BadStruct {
  @Address int value(); // rejected
}
```

Tests:

- `ForeignInterfaceProcessorTest.generatesPassModeFFM`
- `ForeignMemoryProcessorTest.generatesTransitiveRecordAddressValueField`
- `ForeignMemoryProcessorTest.failsAddressPrimitiveFieldOnInterfaceStruct`

Fix direction:

- Generate foreign-call parameter temporaries through the method-local confined
  arena.
- Generate foreign-call return dereferencing from the returned address.
- Generate record-owned field writes only through allocator-taking APIs.
- Reject memory-backed interface fields with a focused diagnostic.
