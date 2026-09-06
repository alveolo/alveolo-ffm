package pkg;

import java.lang.foreign.SegmentAllocator;

import org.alveolo.ffm.CallState;
import org.alveolo.ffm.FirstVariadicArg;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;
import org.alveolo.ffm.Virtual;

@Struct
interface Pair {
  int left();
  int right();
}

@CallState("errno")
interface ErrorState {
  int value();
}

@Struct(vtable = true)
interface VirtualPairs {
  @Virtual(0)
  @Value Pair make(SegmentAllocator allocator, int left, int right);

  @Virtual(1)
  @FirstVariadicArg(1)
  @Value Pair variadic(SegmentAllocator allocator,
      ErrorState capture, int count, int value);
}
