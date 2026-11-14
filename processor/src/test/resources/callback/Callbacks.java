package pkg;

@org.alveolo.ffm.Callback
interface Binary {
  int apply(int left, int right);
}

@org.alveolo.ffm.Callback
interface EventSpec {
  void invoke(
      java.lang.foreign.MemorySegment context,
      int code,
      java.lang.foreign.MemorySegment value);
}

@org.alveolo.ffm.Callback
interface TickSpec {
  void invoke();
}
