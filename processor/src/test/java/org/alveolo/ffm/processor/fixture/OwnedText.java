package org.alveolo.ffm.processor.fixture;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Release;
import org.alveolo.ffm.Require;

@Require(OwnedText.Support.class)
@Release("free")
@Target(TYPE_USE)
@Retention(CLASS)
public @interface OwnedText {
  interface Support {
    void free(MemorySegment pointer);
  }
}
