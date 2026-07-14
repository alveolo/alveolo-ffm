package org.alveolo.ffm.benchmark.nativecall;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
      var result = run(command, outputDir);
      if (result.exitCode() == 0) return;

      failures.add(String.join(" ", command) + System.lineSeparator()
          + result.output());
    }

    fail("No usable C compiler found. Install cc, gcc, clang, or cl, "
        + "or set CC to a usable compiler." + System.lineSeparator()
        + String.join(System.lineSeparator(), failures));
  }

  @Test
  void callsPrimitiveFunction() {
    assertEquals(42, AffmTestFFM.INSTANCE$F.add_ints(19, 23));
  }

  @Test
  void passesUtf8String() {
    assertEquals(0L, AffmTestFFM.INSTANCE$F.utf8_bytes(""));
    assertEquals(6L, AffmTestFFM.INSTANCE$F.utf8_bytes("ASCII!"));
    assertEquals(12L, AffmTestFFM.INSTANCE$F.utf8_bytes("Юникод"));
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
          .pair_box_interface_address_sum(new PairBoxIA(pair)));
    }
  }

  @Test
  void passesNestedInterfaceStructByValue() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE$F
          .pair_box_interface_value_sum(new PairBoxIV(pair)));
    }
  }

  @Test
  void copiesCountedPrimitivePrefixInAndOut() {
    var values = new int[] {1, 2, 3, 4};

    AffmTestFFM.INSTANCE$F.scale_ints(values, 2, 10);

    assertArrayEquals(new int[] {10, 20, 3, 4}, values);
  }

  @Test
  void acceptsEmptyCountedPrimitivePrefix() {
    var values = new int[] {1, 2, 3};

    AffmTestFFM.INSTANCE$F.scale_ints(values, 0, 10);

    assertArrayEquals(new int[] {1, 2, 3}, values);
  }

  @Test
  void rejectsCountOutsidePrimitiveArray() {
    var values = new int[] {1, 2, 3};

    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.scale_ints(values, -1, 10));
    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.scale_ints(values, 4, 10));
  }

  @Test
  void copiesCountedRecordPrefixInAndOut() {
    var untouched = new PairR(5, 6);
    var values = new PairR[] {
        new PairR(1, 2), new PairR(3, 4), untouched
    };

    AffmTestFFM.INSTANCE$F.offset_pairs(values, 2, 10);

    assertArrayEquals(new PairR[] {
        new PairR(11, 12), new PairR(13, 14), untouched
    }, values);
    assertSame(untouched, values[2]);
  }

  @Test
  void copiesOutCountedRecordPrefixFromNullEntries() {
    var untouched = new PairR(9, 10);
    var values = new PairR[] {null, null, untouched};

    AffmTestFFM.INSTANCE$F.fill_pairs(values, 2, 20);

    assertArrayEquals(new PairR[] {
        new PairR(20, 21), new PairR(22, 23), untouched
    }, values);
    assertSame(untouched, values[2]);
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

    AffmTestFFM.INSTANCE$F.increment_bytes(values, values.remaining());

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
  void rejectsCountOutsideBufferRemainingRegion() {
    var values = ByteBuffer.allocate(3);
    values.position(1);

    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.increment_bytes(values, -1));
    assertThrows(IllegalArgumentException.class,
        () -> AffmTestFFM.INSTANCE$F.increment_bytes(values, 3));
  }

  @Test
  void copiesOutHeapTypedBufferWhenAnnotatedOut() {
    var values = IntBuffer.allocate(2);

    AffmTestFFM.INSTANCE$F.fill_two_int_buffer(values, 19);

    assertEquals(19, values.get(0));
    assertEquals(20, values.get(1));
  }

  @Test
  void passesOnlyCountedPrefixOfDirectTypedBuffer() {
    var values = ByteBuffer.allocateDirect(4 * Integer.BYTES)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer();
    values.put(new int[] {1, 2, 3, 4});
    values.position(1);

    AffmTestFFM.INSTANCE$F.scale_int_buffer(values, 2, 10);

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
    if (isWindows()) result.add(List.of("cl"));

    return result;
  }

  private static List<String> command(
      List<String> compiler, Path source, Path output, Path outputDir) {
    var command = new ArrayList<>(compiler);

    if (isCl(compiler.getFirst())) {
      command.add("/nologo");
      command.add("/LD");
      command.add(source.toString());
      command.add("/Fe:" + output);
      return command;
    }

    if (isMac()) command.add("-dynamiclib");
    else command.add("-shared");

    if (!isWindows()) command.add("-fPIC");

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
