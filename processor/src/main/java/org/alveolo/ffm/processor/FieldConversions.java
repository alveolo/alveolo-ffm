package org.alveolo.ffm.processor;

/// Conversion expressions shared by scalar and indexed field accessors.
/// Callers supply the storage location and retain layout and indexing logic.
final class FieldConversions {
  private FieldConversions() {}

  static String readValue(TypeGenerator field, String segment) {
    var conversion = field.isRecord()
        ? field.foreignMemoryClassName() + ".fromMemorySegment$F"
        : "new " + field.foreignMemoryClassName();
    return conversion + "(\n    " + segment.replace("\n", "\n    ") + ")";
  }

  static String readAddress(TypeGenerator field, String address) {
    return field.foreignMemoryClassName() + ".reinterpret$F(" + address + ")";
  }

  static String writeValue(TypeGenerator field, String value, String segment,
      String byteSize, String allocator) {
    if (field.isRecord())
      return field.foreignMemoryClassName() + ".toMemorySegment$F(\n    "
          + value + ", " + segment
          + (allocator == null ? "" : ", " + allocator) + ");";

    return "java.lang.foreign.MemorySegment.copy(\n    "
        + memorySegment(field, value) + ", 0L,\n    "
        + segment + ", 0L, " + byteSize + ");";
  }

  static String addressValue(TypeGenerator field, String value,
      String allocator) {
    var address = field.isRecord()
        ? field.foreignMemoryClassName() + ".toMemorySegment$F("
            + allocator + ", " + value + ")"
        : memorySegment(field, value);
    return nullableAddress(value, address);
  }

  static String nullableAddress(String value, String address) {
    return value + " == null ? java.lang.foreign.MemorySegment.NULL\n    : "
        + address;
  }

  private static String memorySegment(TypeGenerator field, String value) {
    if (field.isMemorySegment()) return value;
    return (field.isForeignMemoryImplementation() ? value
        : "((" + field.foreignMemoryClassName() + ") " + value + ")")
        + ".MemorySegment$F";
  }
}
