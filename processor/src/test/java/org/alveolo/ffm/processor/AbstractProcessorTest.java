package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.Element;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignName;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;
import org.alveolo.ffm.ForeignUtils;
import org.alveolo.ffm.ForeignValue;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.macos.Framework;
import org.alveolo.ffm.macos.Frameworks;

import com.google.testing.compile.Compilation;

abstract class AbstractProcessorTest {
  private static final Class<?>[] CORE_CLASSES = {
    ForeignInterface.class,
    ForeignName.class,
    ForeignValue.class,
    ForeignStruct.class,
    ForeignUnion.class,
    Element.class,
    Address.class,
    Sequence.class,
    Framework.class,
    Frameworks.class,
    ForeignUtils.class
  };

  protected JavaFileObject forTestResource(String resource) {
    return forResource(getClass().getResource("/" + resource));
  }

  protected Compilation compile(JavaFileObject... sources) {
    var files = Arrays.stream(CORE_CLASSES)
        .map(Class::getProtectionDomain)
        .map(ProtectionDomain::getCodeSource)
        .map(CodeSource::getLocation)
        .map(URL::getPath)
        .map(File::new)
        .toList();
    return javac()
        .withClasspath(files)
        .withProcessors(
            new ForeignValueProcessor(),
            new ForeignMemoryProcessor(),
            new ForeignInterfaceProcessor())
        .compile(sources);
  }

  protected Compilation compile(String... paths) {
    return compile(Stream.of(paths)
        .map(this::forTestResource)
        .toArray(JavaFileObject[]::new));
  }

  protected void assertGenerated(
      Compilation compilation, String name, String path) {
    assertThat(compilation).generatedSourceFile(name)
        .hasSourceEquivalentTo(forTestResource(path));
  }

  protected static String getGeneratedSource(Compilation compilation,
      String qualifiedName) {
    Optional<JavaFileObject> file =
        compilation.generatedSourceFile(qualifiedName);
    assertTrue(file.isPresent(),
        "Expected generated source file for " + qualifiedName);
    try {
      return file.get().getCharContent(true).toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected static void assertContains(String source, String substring) {
    assertTrue(source.contains(substring),
        "Expected source to contain \"" + substring + "\"\nActual:\n" + source);
  }

  protected static void assertNotContains(String source, String substring) {
    assertTrue(!source.contains(substring),
        "Expected source NOT to contain \"" + substring + "\"\nActual:\n"
            + source);
  }

  protected static void assertErrorContains(Compilation compilation,
      String substring) {
    for (Diagnostic<? extends JavaFileObject> diagnostic : compilation
        .diagnostics()) {
      if (diagnostic.getKind() == Diagnostic.Kind.ERROR
          && diagnostic.getMessage(null) != null
          && diagnostic.getMessage(null).contains(substring))
        return;
    }
    throw new AssertionError(
        "Expected error containing \"" + substring + "\" not found. "
            + "Diagnostics: " + compilation.diagnostics());
  }
}
