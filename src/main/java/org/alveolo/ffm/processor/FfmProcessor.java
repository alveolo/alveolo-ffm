package org.alveolo.ffm.processor;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.SourceVersion.RELEASE_21;
import static org.alveolo.ffm.processor.ProcessorUtils.getValueLayoutString;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import org.alveolo.ffm.macos.Framework;
import org.alveolo.ffm.macos.Frameworks;

@SupportedAnnotationTypes("org.alveolo.ffm.FFM")
@SupportedSourceVersion(RELEASE_21)
public class FfmProcessor extends AbstractProcessor {
	@Override
	public boolean process(
			Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		var messager = processingEnv.getMessager();

		messager.printNote("FfmProcessor process...");
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

		if (type.getKind() != ElementKind.INTERFACE) {
			messager.printError(
					"@FFM is only allowed on interfaces", type);
			return;
		}

		String srcClassName = type.getQualifiedName().toString();
		String packageName = null;
		int lastDot = srcClassName.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = srcClassName.substring(0, lastDot);
		}
		// String srcSimpleClassName = srcClassName.substring(lastDot + 1);
		String className = srcClassName + "FFM";
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

			out.print("  private ");
			out.print(simpleClassName);
			out.println("() {}");
			out.println();

			out.println("  static {");

			frameworks(type).forEach(f -> writeLoad(out, f));

			out.println("  }");
			out.println();

			out.print("  private static final Linker FF$LINKER =");
			out.println(" Linker.nativeLinker();");

			out.print("  private static final SymbolLookup FF$LOOKUP =");
			if (frameworks(type).findAny().isPresent()) {
				out.println(" SymbolLookup.loaderLookup();");
			} else {
				out.println(" FF$LINKER.defaultLookup();");
			}

			for (var member : type.getEnclosedElements()) {
				if (member instanceof ExecutableElement method) {
					writeExecutable(out, method);
				}
			}

			out.println("}");
		}
	}

	private void writeLoad(PrintWriter out, Framework f) {
		String framework = f.value();
		out.print("    System.load(\"/System/Library/Frameworks/");
		out.print(framework);
		out.print(".framework/Versions/");
		out.print(f.version());
		out.print("/");
		out.print(framework);
		out.println("\");");	
	}

	private void writeExecutable(PrintWriter out, ExecutableElement method) {
		if (method.getKind() != ElementKind.METHOD || method.isDefault()
				|| method.getModifiers().contains(Modifier.STATIC)) {
			return;
		}

		if (checkParameterTypes(method.getParameters())) {
			return;
		}

		out.println();

		var returnType = method.getReturnType();

		var mhName = "MH$" + method.getSimpleName();
		out.print("  private static final java.lang.invoke.MethodHandle ");
		out.print(mhName);
		out.print(" = FF$LINKER.downcallHandle(FF$LOOKUP.find(\"");
		out.print(method.getSimpleName());
		out.print("\").get(), ");
		if (returnType.getKind() == TypeKind.VOID) {
			out.print("FunctionDescriptor.ofVoid(");
		} else {
			out.print("FunctionDescriptor.of(");
			out.print(getValueLayoutString(returnType));
			if (!method.getParameters().isEmpty()) {
				out.print(", ");
			}
		}
		out.println(method.getParameters().stream()
				.map(param -> getValueLayoutString(param.asType()))
				.collect(joining(", ", "", "));")));

		out.print("  public static ");
		out.print(returnType);
		out.print(" ");
		out.print(method.getSimpleName()); // TODO override name with annotation

		out.println(method.getParameters().stream()
				.map(param -> param.asType() + " " + param.toString())
				.collect(joining(", ", "(", ") {")));

		out.println("    try {");

		if (returnType.getKind() != TypeKind.VOID) {
			out.print("      return (");
			out.print(returnType);
			out.print(") ");
		} else {
			out.print("      ");
		}
		out.print(mhName);
		out.println(method.getParameters().stream()
				.map(VariableElement::toString)
				.collect(joining(", ", ".invokeExact(", ");")));

		out.println("    } catch (RuntimeException|Error ff$e) {");
		out.println("      throw ff$e;");
		out.println("    } catch (Throwable ff$t) {");
		out.println("      throw new AssertionError(ff$t);");
		out.println("    }");
		out.println("  }");
	}

	/**
	 * @return true if any of the method parameters has unsupported type 
	 */
	private boolean checkParameterTypes(List<? extends VariableElement> params) {
		boolean hasUnsupported = false;

		for (var p : params) {
			var typeMirror = p.asType();
			try {
				getValueLayoutString(typeMirror);
			} catch (IllegalArgumentException e) {
				hasUnsupported = true;

				processingEnv.getMessager()
						.printError("Type is not supported: " + typeMirror, p);
			}
		}

		return hasUnsupported;
	}

	private Stream<Framework> frameworks(TypeElement type) {
		var frameworks = type.getAnnotation(Frameworks.class);
		if (frameworks != null) {
			return Stream.of(frameworks.value());
		}

		var framework = type.getAnnotation(Framework.class);
		if (framework != null) {
			return Stream.of(framework);
		}

		return Stream.of();
	}
}
