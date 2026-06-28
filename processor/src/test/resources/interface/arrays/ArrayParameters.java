package pkg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Sequence;

@ForeignInterface
public interface ArrayParameters {
  void scale(int[] values);

  int sum(@In @Sequence(3) int[] values);

  void fill(@Out @Sequence(2) int[] values);

  void bytes(ByteBuffer values);

  void ints(@Out @Sequence(2) IntBuffer values);
}
