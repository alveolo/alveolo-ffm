package org.alveolo.ffm.processor;

import static javax.lang.model.SourceVersion.RELEASE_21;
import static org.alveolo.ffm.processor.ProcessorUtils.getValueLayoutString;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@SupportedAnnotationTypes("org.alveolo.ffm.Struct")
@SupportedSourceVersion(RELEASE_21)
public class StructProcessor extends AbstractProcessor {
	@Override
	public boolean process(
			Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		var messager = processingEnv.getMessager();

		messager.printNote("StructProcessor process...");
		messager.printNote("RoundEnvironment"
				+ ": processingOver = " + roundEnv.processingOver()
				+ ", rootElements = " + roundEnv.getRootElements());

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

		switch (type.getKind()) {
			case RECORD, CLASS: break;
			default: messager.printError(
					"@FFM is only allowed on interfaces", type);
		}

		String srcClassName = type.getQualifiedName().toString();
		String packageName = null;
		int lastDot = srcClassName.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = srcClassName.substring(0, lastDot);
		}
		// String srcSimpleClassName = srcClassName.substring(lastDot + 1);
		String className = srcClassName + "FM";
		String simpleClassName = className.substring(lastDot + 1);

		var file = processingEnv.getFiler().createSourceFile(className);

		try (var out = new PrintWriter(file.openWriter())) {
			if (packageName != null) {
				out.print("package ");
				out.print(packageName);
				out.println(";");
				out.println();
			}

			out.println("import java.lang.foreign.*;");
			out.println();

			out.print("public final class ");
			out.print(simpleClassName);
			out.println(" {");

			out.println("  private final MemorySegment ms;");
			out.println();

			out.print("  private ");
			out.print(simpleClassName);
			out.println("(MemorySegment ms) {");
			out.println("    this.ms = ms;");
			out.println("  }");
			out.println();

			out.print("  private static final MemoryLayout FM$LAYOUT =");
			out.println(" MemoryLayout.structLayout(");
			out.println("    org.alveolo.ffm.ForeignUtils.pad(new MemoryLayout [] {");

			var elements = type.getKind() == ElementKind.RECORD
					? type.getRecordComponents()
					: type.getEnclosedElements().stream()
							.filter(e -> e.getKind() == ElementKind.FIELD
									&& !e.getModifiers().contains(Modifier.STATIC))
							.toList();

			for (var element : elements) {
				out.print("      ");

				var typeMirror = element.asType();
				try {
					out.print(getValueLayoutString(typeMirror));
				} catch (IllegalArgumentException e) {
					processingEnv.getMessager().printError(
							"Type is not supported: " + typeMirror, type);

					out.print("((ValueLayout) null)");
				}

				out.print(".withName(\"");
				out.print(element.getSimpleName());
				out.println("\"),");
			}

			out.println("    }));");
			out.println();

			out.print("  public static ");
			out.print(simpleClassName);
			out.println(" allocate(Arena arena) {");
			out.print("    return new ");
			out.print(simpleClassName);
			out.println("(arena.allocate(FM$LAYOUT.byteSize(), FM$LAYOUT.byteAlignment()));");
			out.println("  }");

			for (var e : elements) {
				writeAccessors(out, e.asType(), e.getSimpleName());
			}

			out.println("}");
		}
	}

	private void writeAccessors(PrintWriter out, TypeMirror type, Name name) {
		out.println();

		out.print("  private static final java.lang.invoke.VarHandle ");
		out.print(name);
		out.println(" =");
		out.print("    FM$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(\"");
		out.print(name);
		out.println("\"));");

		// getter
		out.println();

		out.print("  public ");
		out.print(type);
		out.print(" ");
		out.print(name); // TODO override name with annotation
		out.println("() {");

		out.print("    return (");
		out.print(type);
		out.print(") ");
		out.print(name);
		out.println(".get(ms);");
		out.println("  }");

		// setter
		out.println();

		out.print("  public void ");
		out.print(name); // TODO override name with annotation
		out.print("(");
		out.print(type);
		out.println(" value) {");

		out.print("    ");
		out.print(name);
		out.println(".set(ms, value);");
		out.println("  }");
	}
}
