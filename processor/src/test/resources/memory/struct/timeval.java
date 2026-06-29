package pkg;

import org.alveolo.ffm.*;

@Struct
public interface timeval {
  int tv_sec();
  int tv_usec();
}
