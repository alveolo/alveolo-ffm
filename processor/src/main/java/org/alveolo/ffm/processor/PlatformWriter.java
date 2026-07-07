package org.alveolo.ffm.processor;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/// Converts line endings from '\n' to System.lineSeparator()
public class PlatformWriter extends FilterWriter {
  private final String lineSeparator = System.lineSeparator();

  public PlatformWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(int c) throws IOException {
    if (c == '\n') {
      out.write(lineSeparator);
    } else {
      out.write(c);
    }
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    out.write(str.substring(off, off + len).replace("\n", lineSeparator));
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    out.write(new String(cbuf, off, len).replace("\n", lineSeparator));
  }
}
