package pkg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.alveolo.ffm.CountedBy;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.In;
import org.alveolo.ffm.Out;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;

@Struct
record CallPoint(int x, int y) {}

@ForeignInterface
public interface ArrayParameters {
  void scale(int[] values);

  int sum(@In @Sequence(3) int[] values);

  void fill(@Out @Sequence(2) int[] values);

  void bytes(ByteBuffer values);

  void ints(@Out @Sequence(2) IntBuffer values);

  void flags(boolean[] values);

  void prefix(@CountedBy("count") int[] values, int count);

  int transform(
      @CountedBy("count") CallPoint[] points,
      long count);

  void produce(@Out CallPoint[] points);

  void consume(@In @Sequence(2) CallPoint[] points);

  void readPrefix(
      @In @CountedBy("count") IntBuffer values,
      int count);
}
