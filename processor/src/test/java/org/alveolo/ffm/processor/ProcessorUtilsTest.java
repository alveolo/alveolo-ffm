package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.junit.jupiter.api.Test;

class ProcessorUtilsTest {
  @Test
  void preservesParameterizedEnclosingType() {
    var signature = new AtomicReference<String>();
    var processor = new AbstractProcessor() {
      @Override
      public Set<String> getSupportedAnnotationTypes() {
        return Set.of("*");
      }

      @Override
      public SourceVersion getSupportedSourceVersion() {
        return RELEASE_25;
      }

      @Override
      public boolean process(
          Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
          var type = processingEnv.getElementUtils().getTypeElement("test.Bad");
          var method = ElementFilter.methodsIn(type.getEnclosedElements()).getFirst();
          signature.set(ProcessorUtils.sourceMethodSignature(method));
        }
        return false;
      }
    };
    var source = forSourceString("test.Bad", """
        package test;
        class Outer<T> {
          class Inner<U> {}
        }
        interface Bad {
          Outer<String>.Inner<Integer> tail(long index);
        }
        """);

    assertThat(javac().withProcessors(processor).compile(source)).succeeded();
    assertEquals("""
        public test.Outer<java.lang.String>.Inner<java.lang.Integer> tail(
              long index)""", signature.get());
  }
}
