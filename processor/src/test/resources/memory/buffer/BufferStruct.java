package pkg;

import java.nio.IntBuffer;

import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;

@Struct
public interface BufferStruct {
  @Sequence(3)
  IntBuffer data();
}
