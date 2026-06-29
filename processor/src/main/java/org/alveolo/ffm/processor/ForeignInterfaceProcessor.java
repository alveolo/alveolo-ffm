package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import org.alveolo.ffm.Library;

@SupportedAnnotationTypes("org.alveolo.ffm.ForeignInterface")
@SupportedSourceVersion(RELEASE_25)
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
            var sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            messager.printError(sw.toString(), type);
          }
        }
      }
    }

    return true;
  }

  private void writeFile(TypeElement type) throws IOException {
    var messager = processingEnv.getMessager();

    if (type.getKind() != ElementKind.INTERFACE) {
      messager.printError("@ForeignInterface is only allowed on interfaces",
          type);
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

    var libraries = libraries(type);
    if (!validateLibraries(type, libraries)) return;

    var file = processingEnv.getFiler().createSourceFile(className, type);

    try (var out = file.openWriter()) {
      if (packageName != null) {
        out.write("package " + packageName + ";\n\n");
      }

      out.write("""
          import java.lang.foreign.*;
          import java.lang.invoke.MethodHandle;

          @javax.annotation.processing.Generated(
              "<generator>")
          public final class <name> implements <interface> {
            public static final <name> INSTANCE = new <name>();

            private <name>() {}

          """
          .replace("<generator>", getClass().getCanonicalName())
          .replace("<name>", simpleClassName)
          .replace("<interface>", srcSimpleClassName));

      out.write("""
            private static final Linker FF$LINKER = Linker.nativeLinker();

          """);

      if (libraries.isEmpty()) {
        out.write("""
              private static final SymbolLookup FF$LOOKUP =
                  FF$LINKER.defaultLookup();
            """);
      } else {
        out.write("""
              private static final SymbolLookup FF$LOOKUP = FF$LOOKUP();
            """
        );
        writeLookupInitializer(out, srcSimpleClassName, libraries);
      }

      int index = 0;
      for (var member : type.getEnclosedElements()) {
        if (member instanceof ExecutableElement method) {
          if (method.getKind() != ElementKind.METHOD || method.isDefault()
              || method.getModifiers().contains(Modifier.STATIC)) {
            continue;
          }

          var generator = new ExecutableGenerator(
              processingEnv, method, "FF$MH$" + index++);

          out.write(generator.method());
        }
      }

      out.write("}\n");
    }
  }

  private List<Library> libraries(TypeElement type) {
    return List.of(type.getAnnotationsByType(Library.class));
  }

  private boolean validateLibraries(TypeElement type, List<Library> libraries) {
    var valid = true;

    for (var library : libraries) {
      valid &= validateLibrary(type, library.kind(), library.value(),
          library.version(), "@Library");

      for (var override : library.overrides()) {
        valid &= validateLibrary(type, override.kind(), override.value(), "",
            "@Library.Override");
      }
    }

    return valid;
  }

  private boolean validateLibrary(
      TypeElement type, Library.Kind kind, String value, String version,
      String annotation) {
    var messager = processingEnv.getMessager();

    if (value.isEmpty()) {
      messager.printError(annotation
          + " value is required unless kind is DEFAULT_LOOKUP", type);
      return false;
    }

    if (kind != Library.Kind.NAME && kind != Library.Kind.FRAMEWORK
        && !version.isEmpty()) {
      messager.printError(annotation
          + " version is only supported with Kind.NAME or Kind.FRAMEWORK",
          type);
      return false;
    }

    return true;
  }

  private void writeLookupInitializer(
      Writer out, String sourceClassName, List<Library> libraries)
      throws IOException {
    out.write("""

          private static SymbolLookup FF$LOOKUP() {
            return org.alveolo.ffm.ForeignUtils.libraryLookup(
                <sourceClass>.class,
                FF$LINKER.defaultLookup(),
        <libraries>);
          }
        """
        .replace("<sourceClass>", sourceClassName)
        .replace("<libraries>", librarySpecs(libraries)
            .indent(8)
            .stripTrailing()));
  }

  private String librarySpecs(List<Library> libraries) {
    var result = new StringBuilder();

    for (var i = 0; i < libraries.size(); i++) {
      if (i > 0) {
        result.append(",\n");
      }
      result.append(librarySpec(libraries.get(i)));
    }

    return result.toString();
  }

  private String librarySpec(Library library) {
    return """
        new org.alveolo.ffm.ForeignUtils.LibrarySpec(
            <value>, <version>,
            <os>,
            org.alveolo.ffm.Library.Kind.<kind><overrides>)
        """
        .replace("<value>", quote(library.value()))
        .replace("<version>", quote(library.version()))
        .replace("<os>", osArray(library.os()))
        .replace("<kind>", library.kind().name())
        .replace("<overrides>", libraryOverrides(library))
        .stripTrailing();
  }

  private String libraryOverrides(Library library) {
    var overrides = library.overrides();
    if (overrides.length == 0) return "";

    var result = new StringBuilder();
    for (var override : overrides) {
      result.append(",\n")
          .append(libraryOverride(override)
              .indent(4)
              .stripTrailing());
    }

    return result.toString();
  }

  private String libraryOverride(Library.Override override) {
    return """
        new org.alveolo.ffm.ForeignUtils.LibraryOverride(
            <os>,
            org.alveolo.ffm.Library.Kind.<kind>,
            <value>
        )
        """
        .replace("<os>", osArray(override.os()))
        .replace("<kind>", override.kind().name())
        .replace("<value>", quote(override.value()))
        .stripTrailing();
  }

  private String osArray(Library.OS[] oses) {
    var result = new StringBuilder("new org.alveolo.ffm.Library.OS[] {");
    for (var i = 0; i < oses.length; i++) {
      if (i > 0) {
        result.append(", ");
      }
      result.append("org.alveolo.ffm.Library.OS.")
          .append(oses[i].name());
    }
    return result.append("}").toString();
  }

  private static String quote(String value) {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }
}
