package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_25;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignInterfaceClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.foreignInterfaceSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.packageName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateSimpleClassName;
import static org.alveolo.ffm.processor.ProcessorUtils.validateTopLevelType;

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
import javax.tools.Diagnostic;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;

@SupportedAnnotationTypes("org.alveolo.ffm.ForeignInterface")
@SupportedSourceVersion(RELEASE_25)
public class ForeignInterfaceProcessor extends AbstractProcessor {
  @Override
  public boolean process(
      Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return true;

    var messager = processingEnv.getMessager();

    for (var annotation : annotations) {
      var ffmElements = roundEnv.getElementsAnnotatedWith(annotation);

      for (var ffm : ffmElements) {
        if (ffm instanceof TypeElement type) {
          try {
            var fi = type.getAnnotation(ForeignInterface.class);
            if (fi != null) {
              validateSimpleClassName(annotation, fi, fi.name());
              validateTopLevelType(type, fi);
              writeFile(type);
            }
          } catch (ProcessorError e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                e.getMessage(), e.getElement());
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

  private void writeFile(TypeElement iface) throws IOException {
    var messager = processingEnv.getMessager();

    if (iface.getKind() != ElementKind.INTERFACE) {
      messager.printError("@ForeignInterface is only allowed on interfaces",
          iface);
      return;
    }

    var elements = processingEnv.getElementUtils();
    String packageName = packageName(iface, elements);
    String ifaceSimpleName = iface.getSimpleName().toString();
    String className = foreignInterfaceClassName(iface, elements);
    String simpleClassName = foreignInterfaceSimpleClassName(iface);

    var libraries = libraries(iface);
    if (!validateLibraries(iface, libraries)) return;

    var file = processingEnv.getFiler().createSourceFile(className, iface);

    try (var out = new PlatformWriter(file.openWriter())) {
      if (!packageName.isEmpty()) {
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
          .replace("<interface>", ifaceSimpleName));

      out.write("""
            public static final Linker FF$LINKER = Linker.nativeLinker();

          """);

      if (libraries.isEmpty()) {
        out.write("""
              public static final SymbolLookup FF$LOOKUP =
                  FF$LINKER.defaultLookup();
            """);
      } else {
        out.write("""
              public static final SymbolLookup FF$LOOKUP = FF$LOOKUP();
            """
        );
        writeLookupInitializer(out, ifaceSimpleName, libraries);
      }

      int index = 0;
      for (var member : iface.getEnclosedElements()) {
        if (member instanceof ExecutableElement method) {
          if (method.getKind() != ElementKind.METHOD
              || !method.getModifiers().contains(Modifier.ABSTRACT)) {
            continue;
          }

          var generator = new ExecutableGenerator(
              processingEnv, method, "FF$MH$" + index++);

          out.write(generator.methodWithHandle());
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
      messager.printError(annotation + " value is required", type);
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
