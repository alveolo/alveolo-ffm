package org.alveolo.ffm.processor;

import javax.lang.model.type.TypeMirror;

public class ProcessorUtils {
	static String getValueLayoutString(TypeMirror typeMirror) {
		String type = typeMirror.toString();

		return switch (type) {
			case "java.lang.foreign.MemorySegment" -> "ValueLayout.ADDRESS";
			case "int" -> "ValueLayout.JAVA_INT";
			case "long" -> "ValueLayout.JAVA_LONG";
			case "float" -> "ValueLayout.JAVA_FLOAT";
			case "double" -> "ValueLayout.JAVA_DOUBLE";
			case "short" -> "ValueLayout.JAVA_SHORT";
			case "char" -> "ValueLayout.JAVA_CHAR";
			case "byte" -> "ValueLayout.JAVA_BYTE";

			default -> throw new IllegalArgumentException(
					"Unsupported type: " + type); // TODO custom structures
		};
	}
}
