package org.alveolo.ffm.benchmark.nativecall;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.foreign.Arena;
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
    assertEquals(42, AffmTestFFM.INSTANCE.add_ints(19, 23));
  }

  @Test
  void passesUtf8String() {
    assertEquals(0L, AffmTestFFM.INSTANCE.utf8_bytes(""));
    assertEquals(6L, AffmTestFFM.INSTANCE.utf8_bytes("ASCII!"));
    assertEquals(12L, AffmTestFFM.INSTANCE.utf8_bytes("Юникод"));
  }

  @Test
  void returnsRecordStructByValue() {
    var pair = AffmTestFFM.INSTANCE.make_pair_record(7, 11);
    assertEquals(7, pair.left());
    assertEquals(11, pair.right());
  }

  @Test
  void passesRecordStructByValue() {
    assertEquals(18, AffmTestFFM.INSTANCE
        .pair_sum(new PairR(7, 11)));
  }

  @Test
  void passesRecordStructByAddress() {
    assertEquals(18, AffmTestFFM.INSTANCE
        .pair_ptr_sum_record(new PairR(7, 11)));
  }

  @Test
  void returnsInterfaceStructWithAllocator() {
    try (var arena = Arena.ofConfined()) {
      var pair = AffmTestFFM.INSTANCE.make_pair(arena, 7, 11);
      assertEquals(7, pair.left());
      assertEquals(11, pair.right());
    }
  }

  @Test
  void passesInterfaceStructByValue() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE
          .pair_sum_interface_value(pair));
    }
  }

  @Test
  void passesInterfaceStructByAddress() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE
          .pair_ptr_sum_interface(pair));
    }
  }

  @Test
  void passesNestedRecordStructByValue() {
    assertEquals(18, AffmTestFFM.INSTANCE
        .pair_box_record_value_sum(
            new PairBoxRV(new PairR(7, 11))));
  }

  @Test
  void passesNestedRecordStructByAddress() {
    assertEquals(18, AffmTestFFM.INSTANCE
        .pair_box_record_address_sum(
            new PairBoxRA(new PairR(7, 11))));
  }

  @Test
  void passesNestedInterfaceStructByAddress() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE
          .pair_box_interface_address_sum(new PairBoxIA(pair)));
    }
  }

  @Test
  void passesNestedInterfaceStructByValue() {
    try (var arena = Arena.ofConfined()) {
      var pair = new PairSFM(arena).left(7).right(11);
      assertEquals(18, AffmTestFFM.INSTANCE
          .pair_box_interface_value_sum(new PairBoxIV(pair)));
    }
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
