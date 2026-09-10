package org.alveolo.ffm.processor;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/// NIO buffer carriers and views used by call parameters and indexed fields.
enum BufferType {
  BYTE(ByteBuffer.class, ""),
  CHAR(CharBuffer.class),
  SHORT(ShortBuffer.class),
  INT(IntBuffer.class),
  LONG(LongBuffer.class),
  FLOAT(FloatBuffer.class),
  DOUBLE(DoubleBuffer.class);

  final Class<? extends Buffer> type;
  final String conversion;

  BufferType(Class<? extends Buffer> type) {
    this(type, ".as" + type.getSimpleName() + "()");
  }

  BufferType(Class<? extends Buffer> type, String conversion) {
    this.type = type;
    this.conversion = conversion;
  }

  static BufferType forType(String typeName) {
    for (var buffer : values()) {
      if (buffer.type.getCanonicalName().equals(typeName))
        return buffer;
    }
    return null;
  }

  static BufferType forPrimitive(TypeMirror typeMirror) {
    return forPrimitive(typeMirror.getKind());
  }

  static BufferType forPrimitive(TypeKind kind) {
    // Boolean array fields expose their storage through a byte buffer.
    return kind == TypeKind.BOOLEAN ? BYTE : valueOf(kind.name());
  }
}
