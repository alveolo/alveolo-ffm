package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.tools.JavaFileObject;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.ForeignName;
import org.alveolo.ffm.ForeignStruct;
import org.alveolo.ffm.ForeignUnion;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.macos.Framework;
import org.alveolo.ffm.macos.Frameworks;

import com.google.testing.compile.Compilation;

abstract class AbstractProcessorTest {
  private static final Class<?>[] CORE_CLASSES = {
    ForeignInterface.class,
    ForeignName.class,
    ForeignStruct.class,
    ForeignUnion.class,
    Address.class,
    Sequence.class,
    Framework.class,
    Frameworks.class,
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
}
