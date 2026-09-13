package org.alveolo.ffm.benchmark.nativecall;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Library;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Release;
import org.alveolo.ffm.Require;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Symbol;

@ForeignInterface
@Library("affm_test")
public interface OwnedTextApi extends OwnedTextSupport {
  @OwnedCString String owned_text(int kind);

  @OwnedCString String owned_text_bad_copy(@Out OwnedPointee[] output);

  @Symbol("owned_text_bad_copy")
  @Release("releaseAndFail") String badCopyAndRelease(@Out OwnedPointee[] output);

  @Symbol("owned_text")
  @Release("missing_release") String missingRelease(int kind);

  void missing_release(MemorySegment pointer);
}

interface OwnedTextSupport {
  void release_text(MemorySegment pointer);

  int text_allocations();
  int text_releases();

  default void releaseAndFail(MemorySegment pointer) {
    release_text(pointer);
    throw new IllegalStateException("release failed");
  }
}

@Require(OwnedTextSupport.class)
@Release("release_text")
@Target(TYPE_USE)
@Retention(CLASS)
@interface OwnedCString {}

@Struct
record OwnedPointee(@Address int value) {}
