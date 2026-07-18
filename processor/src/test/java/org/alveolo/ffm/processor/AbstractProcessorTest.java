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
import org.alveolo.ffm.CallState;
import org.alveolo.ffm.CountedBy;
import org.alveolo.ffm.DispatchTable;
import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Libraries;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Slot;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;
import org.alveolo.ffm.Union;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.Virtual;
import org.alveolo.ffm.macos.CFString;
import org.alveolo.ffm.macos.CFStringSupport;

import com.google.testing.compile.Compilation;

abstract class AbstractProcessorTest {
  private static final Class<?>[] CORE_CLASSES = {
    Address.class,
    CallState.class,
    CountedBy.class,
    CFString.class,
    CFStringSupport.class,
    DispatchTable.class,
    FirstVariadicArg.class,
    ForeignInterface.class,
    Libraries.class,
    Library.class,
    In.class,
    Out.class,
    Sequence.class,
    Slot.class,
    Struct.class,
    Symbol.class,
    Union.class,
    Value.class,
    Virtual.class,
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
            new CallStateProcessor(),
            new DispatchTableProcessor(),
            new ForeignInterfaceProcessor(),
            new ForeignMemoryProcessor())
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
