package org.alveolo.ffm.benchmark.nativecall;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.alveolo.ffm.ForeignUtils;
import org.alveolo.ffm.NativeType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NativeSharedLibraryTest {
  private static final String LIBRARY_NAME = "affm_test";

  @BeforeAll
  static void compileNativeLibrary() throws Exception {
    var source = Path.of(NativeSharedLibraryTest.class
        .getResource("/native/affm_test.c").toURI());
    var outputDir = Path.of(System.getProperty("user.dir"),
        "target", "native-test");
    Files.createDirectories(outputDir);

    var output = outputDir.resolve(sharedLibraryFileName());
    var failures = new ArrayList<String>();

    for (var compiler : compilers()) {
      var command = command(compiler, source, output, outputDir);
      CompileResult result;
      try {
        result = run(command, outputDir);
      } catch (IOException e) {
        result = new CompileResult(1, e.getMessage());
      }
      if (result.exitCode() == 0) return;

      failures.add(String.join(" ", command) + System.lineSeparator()
          + result.output());
    }

    fail("No usable C compiler found. Install cc, gcc, clang, or cl, "
        + "or set CC to a usable compiler." + System.lineSeparator()
        + String.join(System.lineSeparator(), failures));
  }

  @Test
  void returnsVirtualInterfaceStructInCallerProvidedStorage() {
    var object = AffmTestFFM.INSTANCE$F.get_virtual_pairs();
    try (var arena = Arena.ofConfined()) {
      var storage = PairSFM.allocate$F(arena);
      var pair = object.make(SegmentAllocator.prefixAllocator(storage), 7, 11);

      assertEquals(storage, ((PairSFM) pair).MemorySegment$F);
      assertEquals(7, pair.left());
      assertEquals(11, pair.right());
    }
  }

  @Test
  void capturesCallStateForVirtualInterfaceStructReturn() {
    var object = AffmTestFFM.INSTANCE$F.get_virtual_pairs();
    try (var arena = Arena.ofConfined()) {
      var capture = new Errno(arena);
      var pair = object.makeWithError(arena, capture, 7, 11, 2468);

      assertEquals(7, pair.left());
      assertEquals(11, pair.right());
      assertEquals(2468, capture.errno());
    }
  }

  @Test
  void copiesOutEntireVirtualArrayRegardlessOfCount() {
    var object = AffmTestFFM.INSTANCE$F.get_array_virtual();
    var values = new int[] {1, 2, 3};

    object.fill(values, 1);
    assertArrayEquals(new int[] {10, 0, 0}, values);

    object.fill(values, 0);
    assertArrayEquals(new int[] {0, 0, 0}, values);

    object.fill(values);
    assertArrayEquals(new int[] {10, 11, 12}, values);
  }

  @Test
  void derivesVirtualCountFromBufferWindow() {
    var object = AffmTestFFM.INSTANCE$F.get_array_virtual();
    var values = new int[] {1, 2, 3, 4};
    var buffer = IntBuffer.wrap(values).position(1).limit(3);

    object.fill(buffer);

    assertArrayEquals(new int[] {1, 10, 11, 4}, values);
    assertEquals(1, buffer.position());
    assertEquals(3, buffer.limit());
  }

  @Test
  void passesExplicitVirtualCountsUnchanged() {
    var object = AffmTestFFM.INSTANCE$F.get_array_virtual();
    var values = new int[] {1, 2, 3};

    assertEquals(-1, object.count(values, -1));
    assertEquals(4, object.count(values, 4));
    assertEquals(0, object.count(values, 0));
    assertEquals(3, object.count(values));
  }

  @Test
  void mapsNativeNullReturnsToJavaNull() {
    var api = AffmTestFFM.INSTANCE$F;
    assertNull(api.absentPair());
    assertNull(api.absentUnion());
    assertNull(api.absentWrapper());
    assertNull(api.absentRecord());
    assertNull(api.absentVirtual());
    assertNull(api.absentString());
  }

  @Test
  void distinguishesNativeNullFromHeapStorage() {
    var zeroAddress = MemorySegment.NULL.asReadOnly();
    assertNull(PairSFM.reinterpret$F(zeroAddress));
    assertNull(PairRFM.reinterpret$F(zeroAddress));
    assertNull(PairRFM.reinterpret$F(zeroAddress.reinterpret(8)));
    assertNull(VirtualPairsFM.reinterpret$F(zeroAddress));
    assertNull(NullableUnionFM.reinterpret$F(zeroAddress));
    assertThrows(NullPointerException.class,
        () -> PairSFM.reinterpret$F(null));

    var heap = MemorySegment.ofArray(new int[] {7, 11});
    assertEquals(0, heap.address());
    assertSame(heap, ForeignUtils.requireNonNullAddress(heap));
    assertThrows(NullPointerException.class,
        () -> ForeignUtils.requireNonNullAddress(zeroAddress.reinterpret(8)));
    assertEquals(7, new PairSFM(heap).left());
    assertEquals(new PairR(7, 11), PairRFM.fromMemorySegment$F(heap));
    assertThrows(UnsupportedOperationException.class,
        () -> PairSFM.reinterpret$F(heap));
    assertThrows(UnsupportedOperationException.class,
        () -> PairRFM.reinterpret$F(heap));
  }

  @Test
  void handlesNullInOrdinaryPointerFields() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      var box = new PairBoxIAFM(arena);
      var wrappers = new NullableFieldsFM(arena);
      assertNull(box.pair());
      assertNull(wrappers.pair());
      box.pair(pair);
      wrappers.pair(pair);
      assertEquals(7, box.pair().left());
      assertEquals(11, wrappers.pair().right());
      box.pair(null);
      wrappers.pair(null);
      assertNull(box.pair());
      assertNull(wrappers.pair());

      var storage = PairBoxRAFM.allocate$F(arena);
      assertEquals(new PairBoxRA(null), PairBoxRAFM.fromMemorySegment$F(storage));
      PairBoxRAFM.pair(storage, arena, new PairR(7, 11));
      assertEquals(new PairR(7, 11), PairBoxRAFM.pair(storage));
      PairBoxRAFM.pair(storage, arena, null);
      assertNull(PairBoxRAFM.pair(storage));
    }
  }

  @Test
  void passesNullableStringsAndStructPointers() {
    var api = AffmTestFFM.INSTANCE$F;
    assertEquals(0, api.optionalPair(null));
    assertEquals(0, api.optionalWrapper(null));
    assertEquals(0, api.optionalRecord(null));
    assertEquals(0, api.optionalString(null));
    assertEquals(1000, api.optionalString(""));
    assertEquals(1003, api.optionalString("abc"));
    assertEquals(118, api.optionalRecord(new PairR(7, 11)));
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(118, api.optionalPair(pair));
      assertEquals(118, api.optionalWrapper(pair));
      assertEquals(1003, api.optionalInterfaceValues(null, "abc"));
      assertEquals(118, api.optionalInterfaceValues(pair, null));
    }
  }

  @Test
  void handlesNullableValuesWithSharedAndDirectAllocation() {
    var api = AffmTestFFM.INSTANCE$F;
    for (var pair : new PairR[] {null, new PairR(7, 11)}) {
      for (var text : new String[] {null, "", "abc"}) {
        var expected = (pair == null ? 0 : 118)
            + (text == null ? 0 : 1000 + text.length());
        assertEquals(expected, api.optionalValues(pair, text));
        assertEquals(expected, api.optionalAllocatingValues(
            pair == null ? null : new PairBoxRA(pair), text));
      }
    }
    assertEquals(1003, api.optionalAllocatingValues(new PairBoxRA(null), "abc"));
  }

  @Test
  void rejectsNullByValueStructs() {
    var api = AffmTestFFM.INSTANCE$F;
    assertThrows(NullPointerException.class, () -> api.pair_sum(null));
    assertThrows(NullPointerException.class, () -> api.requiredValues(null, "abc"));
    assertEquals(118, api.requiredValues(new PairR(7, 11), null));
    assertEquals(1121, api.requiredValues(new PairR(7, 11), "abc"));
    assertThrows(NullPointerException.class,
        () -> api.pair_sum_interface_value(null));
    try (var arena = Arena.ofConfined()) {
      var box = new PairBoxIVFM(arena);
      assertThrows(NullPointerException.class, () -> box.pair(null));
      assertThrows(NullPointerException.class,
          () -> PairRFM.toMemorySegment$F(arena, null));
    }
  }

  @Test
  void rejectsNativeNullBeforePrimitivePointerAccess() {
    var api = AffmTestFFM.INSTANCE$F;
    var plain = assertThrows(NullPointerException.class, api::absentInt);
    var canonical = assertThrows(NullPointerException.class, api::absentSize);
    assertEquals("Cannot dereference a native NULL pointer", plain.getMessage());
    assertEquals(plain.getMessage(), canonical.getMessage());
    try (var arena = Arena.ofConfined()) {
      var primitive = PrimitivePointerFM.allocate$F(arena);
      var size = SizeValueFM.allocate$F(arena);
      assertThrows(NullPointerException.class,
          () -> PrimitivePointerFM.value(primitive));
      assertThrows(NullPointerException.class, () -> SizeValueFM.pointer(size));
      PrimitivePointerFM.value(primitive, arena, 0);
      SizeValueFM.pointer(size, arena, 0);
      assertEquals(0, PrimitivePointerFM.value(primitive));
      assertEquals(0, SizeValueFM.pointer(size));
    }
  }

  @Test
  void callsPrimitiveFunction() {
    assertEquals(42, AffmTestFFM.INSTANCE$F.add_ints(19, 23));
  }

  @Test
  void adaptsCanonicalCScalarsWithoutChangingJavaCarriers() {
    assertEquals(-123L, AffmTestFFM.INSTANCE$F.echo_slong(-123L));
    assertEquals(0xffff_ffffL,
        AffmTestFFM.INSTANCE$F.echo_ulong(0xffff_ffffL));
    assertEquals(0xffff_ffffL,
        AffmTestFFM.INSTANCE$F.echo_size_t(0xffff_ffffL));
    assertEquals(0xffff, AffmTestFFM.INSTANCE$F.echo_wchar(0xffff));
  }

  @Test
  void combinesAddressWithCanonicalScalarPointees() {
    assertEquals(321L, AffmTestFFM.INSTANCE$F.read_c_long(321L));
    assertEquals(123L, AffmTestFFM.INSTANCE$F.c_long_address());
  }

  @Test
  void adaptsSizeTCallsAndPointersToNativeWidth() {
    var api = AffmTestFFM.INSTANCE$F;
    var maximum = NativeType.SIZE_T.layout.byteSize() == 4
        ? 0xffff_ffffL : -1L;
    assertEquals(maximum, api.size_t_address());
    assertEquals(0, api.echo_size_t(0));
    assertEquals(maximum, api.echo_size_t(maximum));
    assertEquals(maximum, api.read_size_t(maximum));
    assertEquals(0xffff_ffffL, api.sum_size_t(0x8000_0000L, 0x7fff_ffffL));
    if (NativeType.SIZE_T.layout.byteSize() == 4) {
      for (var value : new long[] {-1, 0x1_0000_0000L}) {
        assertThrows(ArithmeticException.class, () -> api.echo_size_t(value));
        assertThrows(ArithmeticException.class, () -> api.read_size_t(value));
        assertThrows(ArithmeticException.class, () -> api.sum_size_t(0, value));
      }
    } else {
      assertEquals(Long.MIN_VALUE, api.echo_size_t(Long.MIN_VALUE));
      assertEquals(Long.MIN_VALUE, api.read_size_t(Long.MIN_VALUE));
      assertEquals(Long.MIN_VALUE, api.sum_size_t(Long.MAX_VALUE, 1));
    }
  }

  @Test
  void adaptsSizeTStructFieldsAndRecordSnapshots() {
    var maximum = NativeType.SIZE_T.layout.byteSize() == 4
        ? 0xffff_ffffL : -1L;
    SizeValue snapshot;
    try (var arena = Arena.ofConfined()) {
      var field = new SizeFieldFM(arena);
      field.value(maximum);
      assertEquals(maximum, field.value());
      var value = new SizeValue(maximum, maximum);
      var segment = SizeValueFM.toMemorySegment$F(arena, value);
      snapshot = SizeValueFM.fromMemorySegment$F(segment);
      assertEquals(value, snapshot);
      if (NativeType.SIZE_T.layout.byteSize() == 4) {
        for (var invalid : new long[] {-1, 0x1_0000_0000L}) {
          assertThrows(ArithmeticException.class, () -> field.value(invalid));
          assertThrows(ArithmeticException.class,
              () -> SizeValueFM.value(segment, invalid));
          assertThrows(ArithmeticException.class,
              () -> SizeValueFM.pointer(segment, arena, invalid));
        }
      }
    }
    assertEquals(maximum, snapshot.value());
    assertEquals(maximum, snapshot.pointer());
  }

  @Test
  void callsSpecializedVariadicFunctionWithAndWithoutTailArguments() {
    assertEquals(0, AffmTestFFM.INSTANCE$F.variadic_sum(0));
    assertEquals(42, AffmTestFFM.INSTANCE$F.variadic_sum(3, 10, 12, 20));
  }

  @Test
  void passesUtf8String() {
    assertEquals(0L, AffmTestFFM.INSTANCE$F.utf8_bytes(""));
    assertEquals(6L, AffmTestFFM.INSTANCE$F.utf8_bytes("ASCII!"));
    assertEquals(12L, AffmTestFFM.INSTANCE$F.utf8_bytes("Юникод"));
  }

  @Test
  void capturesErrnoAndAppliesApiSpecificFailureCondition() {
    try (var arena = Arena.ofConfined()) {
      var capture = new Errno(arena);

      assertEquals(23, AffmTestFFM.INSTANCE$F
          .checked_errno_return(capture, 23, 1234));
      assertEquals(1234, capture.errno());

      var exception = assertThrows(IllegalStateException.class,
          () -> AffmTestFFM.INSTANCE$F
              .checked_errno_return(capture, -1, 4321));
      assertEquals("native error: 4321", exception.getMessage());
    }
  }

  @Test
  void capturesErrnoForStructReturn() {
    try (var arena = Arena.ofConfined()) {
      var capture = new Errno(arena);

      var pair = AffmTestFFM.INSTANCE$F
          .make_pair_and_set_errno(capture, 7, 11, 2468);

      assertEquals(new PairR(7, 11), pair);
      assertEquals(2468, capture.errno());
    }
  }

  @Test
  void returnsRecordStructByValue() {
    var pair = AffmTestFFM.INSTANCE$F.make_pair_record(7, 11);
    assertEquals(7, pair.left());
    assertEquals(11, pair.right());
  }

  @Test
  void passesRecordStructByValue() {
    assertEquals(18, AffmTestFFM.INSTANCE$F
        .pair_sum(new PairR(7, 11)));
  }

  @Test
  void passesRecordStructByAddress() {
    assertEquals(18, AffmTestFFM.INSTANCE$F
        .pair_ptr_sum_record(new PairR(7, 11)));
  }

  @Test
  void returnsInterfaceStructWithAllocator() {
    try (var arena = Arena.ofConfined()) {
      var pair = AffmTestFFM.INSTANCE$F.make_pair(arena, 7, 11);
      assertEquals(7, pair.left());
      assertEquals(11, pair.right());
    }
  }

  @Test
  void passesInterfaceStructByValue() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE$F
          .pair_sum_interface_value(pair));
    }
  }

  @Test
  void passesInterfaceStructByAddress() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE$F
          .pair_ptr_sum_interface(pair));
    }
  }

  @Test
  void passesNestedRecordStructByValue() {
    assertEquals(18, AffmTestFFM.INSTANCE$F
        .pair_box_record_value_sum(
            new PairBoxRV(new PairR(7, 11))));
  }

  @Test
  void passesNestedRecordStructByAddress() {
    assertEquals(18, AffmTestFFM.INSTANCE$F
        .pair_box_record_address_sum(
            new PairBoxRA(new PairR(7, 11))));
  }

  @Test
  void passesNestedInterfaceStructByAddress() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE$F
          .pair_box_interface_address_sum(new PairBoxIAFM(arena).pair(pair)));
    }
  }

  @Test
  void passesNestedInterfaceStructByValue() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE$F
          .pair_box_interface_value_sum(new PairBoxIVFM(arena).pair(pair)));
    }
  }

  @Test
  void preservesArrayContentsNotModifiedByNativeCall() {
    var values = new int[] {1, 2, 3, 4};

    AffmTestFFM.INSTANCE$F.scale_ints(values, 2, 10);

    assertArrayEquals(new int[] {10, 20, 3, 4}, values);
  }

  @Test
  void transfersArrayWhenNativeCountIsZero() {
    var values = new int[] {1, 2, 3};

    AffmTestFFM.INSTANCE$F.scale_ints(values, 0, 10);

    assertArrayEquals(new int[] {1, 2, 3}, values);
  }

  @Test
  void derivesNativeCountFromEntireArray() {
    var values = new int[] {1, 2, 3};

    AffmTestFFM.INSTANCE$F.scale_ints(values, 10);

    assertArrayEquals(new int[] {10, 20, 30}, values);
    AffmTestFFM.INSTANCE$F.scale_ints(new int[0], 10);
  }

  @Test
  void copiesEveryRecordEvenWhenNativeCountIsSmaller() {
    var untouched = new PairR(5, 6);
    var values = new PairR[] {
      new PairR(1, 2), new PairR(3, 4), untouched
    };

    AffmTestFFM.INSTANCE$F.offset_pairs(values, 2, 10);

    assertArrayEquals(new PairR[] {
      new PairR(11, 12), new PairR(13, 14), untouched
    }, values);
    assertNotSame(untouched, values[2]);
  }

  @Test
  void derivesRecordOutputCountFromArrayLength() {
    var values = new PairR[3];

    AffmTestFFM.INSTANCE$F.fill_pairs(values, 20);

    assertArrayEquals(new PairR[] {
      new PairR(20, 21), new PairR(22, 23), new PairR(24, 25)
    }, values);
  }

  @Test
  void mutatesNativeMatrixInlineRecordsAndNullablePointers() {
    try (var arena = Arena.ofConfined()) {
      var values = new NativeArraysFM(arena)
          .matrix(1, 2, 5)
          .points(1, new PairR(1, 2))
          .pointers(arena, 0, new PairR(3, 4))
          .pointersAsAddress$F(1, java.lang.foreign.MemorySegment.NULL);

      AffmTestFFM.INSTANCE$F.mutate_native_arrays(values);

      assertEquals(105, values.matrix(1, 2));
      assertEquals(77, values.matrix(0, 1));
      assertEquals(new PairR(11, 22), values.points(1));
      assertEquals(new PairR(33, 44), values.pointers(0));
      assertNull(values.pointers(1));
    }
  }

  @Test
  void copiesInArrayParameterOnlyWhenAnnotatedIn() {
    var values = new int[] {1, 2, 3};

    assertEquals(6, AffmTestFFM.INSTANCE$F.sum_three_and_clobber(values));

    assertArrayEquals(new int[] {1, 2, 3}, values);
  }

  @Test
  void passesPrimitiveArrayByValue() {
    var values = new int[] {1, 2, 3};

    assertEquals(6, AffmTestFFM.INSTANCE$F.sum_int3_value(values));

    assertArrayEquals(new int[] {1, 2, 3}, values);
  }

  @Test
  void passesDirectIntBufferByValue() {
    var values = ByteBuffer.allocateDirect(3 * Integer.BYTES)
        .order(ByteOrder.nativeOrder()).asIntBuffer();
    values.put(0, 1).put(1, 2).put(2, 3);

    assertEquals(6, AffmTestFFM.INSTANCE$F.sum_int3_buffer_value(values));

    assertEquals(1, values.get(0));
    assertEquals(2, values.get(1));
    assertEquals(3, values.get(2));
  }

  @Test
  void copiesOutArrayParameterOnlyWhenAnnotatedOut() {
    var values = new int[] {100, 200};

    AffmTestFFM.INSTANCE$F.fill_two_ints(values, 7);

    assertArrayEquals(new int[] {7, 8}, values);
  }

  @Test
  void rejectsArrayParameterWithWrongSequenceLength() {
    var values = new int[] {100};

    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.fill_two_ints(values, 7));
  }

  @Test
  void copiesHeapBufferParameterInAndOutByDefault() {
    var values = ByteBuffer.wrap(new byte[] {1, 2, 3});
    values.position(1);

    AffmTestFFM.INSTANCE$F.increment_bytes(values);

    assertEquals(1, values.get(0));
    assertEquals(3, values.get(1));
    assertEquals(4, values.get(2));
    assertEquals(1, values.position());
  }

  @Test
  void usesDirectBufferParameterWithoutCopiesByDefault() {
    var values = ByteBuffer.allocateDirect(3);
    values.put(0, (byte) 1);
    values.put(1, (byte) 2);
    values.put(2, (byte) 3);
    values.position(1);

    AffmTestFFM.INSTANCE$F.increment_bytes(values, 1);

    assertEquals(1, values.get(0));
    assertEquals(3, values.get(1));
    assertEquals(3, values.get(2));
    assertEquals(1, values.position());
  }

  @Test
  void derivesZeroCountFromEmptyBufferWindow() {
    var bytes = new byte[] {1, 2, 3};
    var values = ByteBuffer.wrap(bytes).position(1).limit(1);

    AffmTestFFM.INSTANCE$F.increment_bytes(values);

    assertArrayEquals(new byte[] {1, 2, 3}, bytes);
    assertEquals(1, values.position());
    assertEquals(1, values.limit());
  }

  @Test
  void copiesOutHeapTypedBufferWhenAnnotatedOut() {
    var values = IntBuffer.allocate(2);

    AffmTestFFM.INSTANCE$F.fill_two_int_buffer(values, 19);

    assertEquals(19, values.get(0));
    assertEquals(20, values.get(1));
  }

  @Test
  void derivesNativeCountFromDirectTypedBufferWindow() {
    var values = ByteBuffer.allocateDirect(4 * Integer.BYTES)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer();
    values.put(new int[] {1, 2, 3, 4});
    values.position(1);

    var window = values.duplicate().limit(3);
    AffmTestFFM.INSTANCE$F.scale_int_buffer(window, 10);
    assertEquals(1, window.position());
    assertEquals(3, window.limit());

    assertEquals(1, values.get(0));
    assertEquals(20, values.get(1));
    assertEquals(30, values.get(2));
    assertEquals(4, values.get(3));
    assertEquals(1, values.position());
  }

  @Test
  void validatesDirectTypedBufferOrderAndReadOnlyState() {
    var nonNative = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
        ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    var wrongOrder = ByteBuffer.allocateDirect(2 * Integer.BYTES)
        .order(nonNative)
        .asIntBuffer();
    var readOnly = IntBuffer.allocate(2).asReadOnlyBuffer();

    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.fill_two_int_buffer(wrongOrder, 1));
    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.fill_two_int_buffer(readOnly, 1));
  }

  @Test
  void inputAnnotationAllowsReadOnlyAndDoesNotCopyDirectStorage() {
    var writable = ByteBuffer.allocateDirect(3 * Integer.BYTES)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer();
    writable.put(new int[] {1, 2, 3});
    writable.position(0);
    var readOnly = writable.asReadOnlyBuffer();

    assertEquals(6,
        AffmTestFFM.INSTANCE$F.sum_three_int_buffer(readOnly));
    assertEquals(777, writable.get(0));
  }

  private static List<List<String>> compilers() {
    var result = new ArrayList<List<String>>();

    var cc = System.getenv("CC");
    if (cc != null && !cc.isBlank()) {
      result.add(List.of(cc.strip().split("\\s+")));
    }

    result.add(List.of("cc"));
    result.add(List.of("gcc"));
    result.add(List.of("clang"));
    if (isWindows()) {
      result.add(List.of("cl"));
    }

    return result;
  }

  private static List<String> command(
      List<String> compiler, Path source, Path output, Path outputDir) {
    var command = new ArrayList<>(compiler);

    if (isCl(compiler.getFirst())) {
      command.add("/nologo");
      command.add("/LD");
      command.add("/MD");
      command.add(source.toString());
      command.add("/Fe:" + output);
      return command;
    }

    if (isMac()) {
      command.add("-dynamiclib");
    } else {
      command.add("-shared");
    }

    if (!isWindows()) {
      command.add("-fPIC");
    }

    command.add(source.toString());
    command.add("-o");
    command.add(output.toString());
    return command;
  }

  private static CompileResult run(List<String> command, Path outputDir)
      throws IOException, InterruptedException {
    var process = new ProcessBuilder(command)
        .directory(outputDir.toFile())
        .redirectErrorStream(true)
        .start();

    if (!process.waitFor(30, SECONDS)) {
      process.destroyForcibly();
      return new CompileResult(1, "Timed out after 30 seconds");
    }

    var output = new String(process.getInputStream().readAllBytes(), UTF_8);
    return new CompileResult(process.exitValue(), output);
  }

  private static String sharedLibraryFileName() {
    if (isWindows()) return LIBRARY_NAME + ".dll";
    if (isMac()) return "lib" + LIBRARY_NAME + ".dylib";
    return "lib" + LIBRARY_NAME + ".so";
  }

  private static boolean isCl(String compiler) {
    var executable = Path.of(compiler).getFileName().toString()
        .toLowerCase(Locale.ROOT);
    return executable.equals("cl") || executable.equals("cl.exe");
  }

  private static boolean isMac() {
    return osName().contains("mac");
  }

  private static boolean isWindows() {
    return osName().contains("win");
  }

  private static String osName() {
    return System.getProperty("os.name").toLowerCase(Locale.ROOT);
  }

  private record CompileResult(int exitCode, String output) {}
}
