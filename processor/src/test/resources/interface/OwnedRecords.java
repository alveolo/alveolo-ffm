package pkg;

import java.lang.foreign.MemorySegment;

import org.alveolo.ffm.Address;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Release;
import org.alveolo.ffm.Struct;

@Struct
record OwnedItem(int value, MemorySegment pointer) {}

@ForeignInterface
public interface OwnedRecords {
  @Address @Release("free") OwnedItem create(@Out int[] values);

  void free(MemorySegment pointer);
}
