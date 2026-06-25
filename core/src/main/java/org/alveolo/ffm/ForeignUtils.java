package org.alveolo.ffm;

import static java.lang.foreign.MemoryLayout.paddingLayout;

import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.SymbolLookup;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ForeignUtils {
  private ForeignUtils() {/* Utility class */}

  public record LibrarySpec(
      String value, String version, Library.OS[] os, Library.Kind kind,
      LibraryOverride... overrides
  ) {}

  public record LibraryOverride(
      Library.OS[] os, Library.Kind kind, String value
  ) {}

  public static SymbolLookup libraryLookup(Class<?> sourceClass,
      SymbolLookup defaultLookup, LibrarySpec... libraries) {
    return Stream.of(libraries)
        .map(lib -> loadPlatformLibrary(sourceClass, defaultLookup, lib))
        .filter(Objects::nonNull)
        .reduce(SymbolLookup::or)
        .orElse(defaultLookup);
  }

  private static SymbolLookup loadPlatformLibrary(
      Class<?> sourceClass, SymbolLookup defaultLookup, LibrarySpec library) {
    var os = os();
    if (!matches(os, library.os())) return null;

    return loadLibrary(sourceClass, os, library.kind(),
        library.value(), library.version(), library.overrides());
  }

  private static SymbolLookup loadLibrary(Class<?> sourceClass,
      Library.OS os, Library.Kind kind, String value, String version,
      LibraryOverride... overrides) {
    var hasOverride = false;
    RuntimeException lastError = null;

    for (var override : overrides) {
      if (!matches(os, override.os())) {
        continue;
      }

      hasOverride = true;
      try {
        return load(sourceClass, override.kind(), override.value(), "");
      } catch (RuntimeException e) {
        if (lastError != null) {
          e.addSuppressed(lastError);
        }
        lastError = e;
      }
    }

    if (hasOverride) throw lastError;

    return load(sourceClass, kind, value, version);
  }

  private static SymbolLookup load(Class<?> sourceClass,
      Library.Kind kind, String value, String version) {
    return switch (kind) {
      case NAME -> loadName(sourceClass, libraryName(value, version));
      case PATH -> loadPath(sourceClass, value);
      case FRAMEWORK -> loadMacFramework(sourceClass, value, version);
    };
  }

  private static SymbolLookup loadName(Class<?> sourceClass, String name) {
    return loadRelocatable(sourceClass, name, false);
  }

  private static SymbolLookup loadPath(Class<?> sourceClass, String path) {
    var library = Path.of(path);
    if (library.isAbsolute()) return loadPathDirect(library);
    return loadRelocatable(sourceClass, path, true);
  }

  private static SymbolLookup loadRelocatable(
      Class<?> sourceClass, String library, boolean path) {
    RuntimeException lastError = null;

    var libraryPath = System.getProperty("affm.library.path");
    if (libraryPath != null && !libraryPath.isBlank()) {
      for (var directory : libraryPath.split(
          Pattern.quote(File.pathSeparator))) {
        if (directory.isBlank()) {
          continue;
        }

        try {
          return loadPathDirect(Path.of(directory, library));
        } catch (RuntimeException e) {
          if (lastError != null) {
            e.addSuppressed(lastError);
          }
          lastError = e;
        }
      }
    }

    var jarDirectory = jarDirectory(sourceClass);
    if (jarDirectory != null) {
      try {
        return loadPathDirect(jarDirectory.resolve(library));
      } catch (RuntimeException e) {
        if (lastError != null) {
          e.addSuppressed(lastError);
        }
        lastError = e;
      }
    }

    try {
      return path
          ? loadPathDirect(Path.of(library))
          : loadNameDirect(library);
    } catch (RuntimeException e) {
      if (lastError != null) {
        e.addSuppressed(lastError);
      }
      throw e;
    }
  }

  private static SymbolLookup loadNameDirect(String name) {
    return SymbolLookup.libraryLookup(name, Arena.global());
  }

  private static SymbolLookup loadPathDirect(Path path) {
    return SymbolLookup.libraryLookup(path, Arena.global());
  }

  private static Path jarDirectory(Class<?> sourceClass) {
    var source = sourceClass.getProtectionDomain().getCodeSource();
    if (source == null) return null;

    try {
      var location = Path.of(source.getLocation().toURI());
      var name = location.getFileName();
      if (name == null || !name.toString().endsWith(".jar")) return null;
      if (!Files.isRegularFile(location)) return null;
      return location.getParent();
    } catch (IllegalArgumentException | URISyntaxException e) {
      return null;
    }
  }

  private static SymbolLookup loadMacFramework(Class<?> sourceClass,
      String name, String version) {
    var path = "/System/Library/Frameworks/" + name + ".framework/";

    return version.isEmpty()
        ? loadName(sourceClass, path + name)
        : loadName(sourceClass, path + "Versions/" + version + "/" + name);
  }

  private static String libraryName(String name, String version) {
    return switch (os()) {
      case MACOS -> "lib" + name + dotVersion(version) + ".dylib";
      case WINDOWS -> name + ".dll";
      default -> "lib" + name + ".so" + dotVersion(version);
    };
  }

  private static String dotVersion(String version) {
    return version.isEmpty() ? "" : "." + version;
  }

  private static boolean matches(Library.OS os, Library.OS[] oses) {
    return oses.length == 0 || Arrays.asList(oses).contains(os);
  }

  private static Library.OS os() {
    var name = System.getProperty("os.name").toLowerCase(Locale.ROOT);

    if (name.contains("mac") || name.contains("darwin"))
      return Library.OS.MACOS;

    if (name.contains("win"))
      return Library.OS.WINDOWS;

    return Library.OS.LINUX;
  }

  public static MemoryLayout[] pad(MemoryLayout... elements) {
    if (elements.length == 0) return elements;

    var result = new ArrayList<MemoryLayout>(elements.length * 2 - 1);

    long offset = 0;

    for (var element : elements) {
      long byteAligment = element.byteAlignment();

      long misAlignment = offset % byteAligment;
      if (misAlignment != 0) {
        var padding = byteAligment - misAlignment;
        result.add(paddingLayout(padding));
        offset += padding;
      }

      result.add(element);
      offset += element.byteSize();
    }

    // Ensure struct will work well in arrays (SequenceLayout)

    long maxAligment = Stream.of(elements)
        .mapToLong(MemoryLayout::byteAlignment).max().getAsLong();

    long misAlignment = offset % maxAligment;
    if (misAlignment != 0) {
      result.add(paddingLayout(maxAligment - misAlignment));
    }

    return result.toArray(MemoryLayout[]::new);
  }
}
