package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_21;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.alveolo.ffm.macos.Framework;
import org.alveolo.ffm.macos.Frameworks;

@SupportedAnnotationTypes("org.alveolo.ffm.ForeignInterface")
@SupportedSourceVersion(RELEASE_21)
public class ForeignInterfaceProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var messager = processingEnv.getMessager();

    messager.printNote("FfmProcessor process...");
    messager.printNote("RoundEnvironment"
        + ": processingOver = " + roundEnv.processingOver()
        + ", rootElements = " + roundEnv.getRootElements());

    if (roundEnv.processingOver()) return true;

    for (var annotation : annotations) {
      messager.printNote("Annotation: " + annotation);

      var ffmElements = roundEnv.getElementsAnnotatedWith(annotation);

      for (var ffm : ffmElements) {
        messager.printNote("Annotated Element: " + ffm);

        if (ffm instanceof TypeElement type) {
          try {
            writeFile(type);
          } catch (Throwable e) {
            messager.printError(e.getMessage(), ffm);
          }
        }
      }
    }

    return true;
  }

  private void writeFile(TypeElement type) throws IOException {
    var messager = processingEnv.getMessager();

    if (type.getKind() != ElementKind.INTERFACE) {
      messager.printError("@FFM is only allowed on interfaces", type);
      return;
    }

    String srcClassName = type.getQualifiedName().toString();
    String packageName = null;
    int lastDot = srcClassName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = srcClassName.substring(0, lastDot);
    }
    String srcSimpleClassName = srcClassName.substring(lastDot + 1);
    String className = srcClassName + "FFM";
    String simpleClassName = className.substring(lastDot + 1);

    var file = processingEnv.getFiler().createSourceFile(className, type);

    try (var out = file.openWriter()) {
      if (packageName != null) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          import java.lang.foreign.*;
          import java.lang.invoke.MethodHandle;

          @javax.annotation.processing.Generated("<generator>")
          public final class <name> implements <interface> {
            public static final <name> INSTANCE = new <name>();

            private <name>() {}

            static {
          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName)
          .replace("<interface>", srcSimpleClassName));

      var frameworks = frameworks(type);
      for (var f : frameworks) {
        writeLoadLibraries(out, f);
      }

      out.write("""
            }

            private static final Linker FF$LINKER = Linker.nativeLinker();

          """);

      out.write("  private static final SymbolLookup FF$LOOKUP =");
      if (frameworks.isEmpty()) {
        out.write(" FF$LINKER.defaultLookup();\n");
      } else {
        out.write(" SymbolLookup.loaderLookup();\n");
      }

      int index = 0;
      for (var member : type.getEnclosedElements()) {
        if (member instanceof ExecutableElement method) {
          var generator = new ExecutableGenerator(
              processingEnv, method, "FF$MH$" + index++);

          if (method.getKind() != ElementKind.METHOD || method.isDefault()
              || method.getModifiers().contains(Modifier.STATIC)
              || generator.checkParameterTypes()) {
            continue;
          }

          out.write(generator.method());
        }
      }

      out.write("}\n");
    }
  }

  private List<Framework> frameworks(TypeElement type) {
    var frameworks = type.getAnnotation(Frameworks.class);
    if (frameworks != null)
      return List.of(frameworks.value());

    var framework = type.getAnnotation(Framework.class);
    if (framework != null)
      return List.of(framework);

    return List.of();
  }

  private void writeLoadLibraries(Writer out, Framework f) throws IOException {
    String framework = f.value();
    String version = f.version();

    out.write("    System.load(\"/System/Library/Frameworks/"
        + framework + ".framework/Versions/" + version + "/"
        + framework + "\");\n");
  }
}
