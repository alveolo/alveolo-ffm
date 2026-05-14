package pkg;

import org.alveolo.ffm.*;

@ForeignStruct
public interface timeval {
  int tv_sec();
  int tv_usec();
}
