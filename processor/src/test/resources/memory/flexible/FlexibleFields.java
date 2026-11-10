package pkg;

import org.alveolo.ffm.Flexible;
import org.alveolo.ffm.ForeignInterface;
import org.alveolo.ffm.Sequence;
import org.alveolo.ffm.Struct;
import org.alveolo.ffm.Value;

@Struct
interface Names {
  int count();
  byte rows(@Flexible("count") long row, @Sequence(10) int column);
}

@Struct
interface Packet {
  int length();
}

@Struct
interface Transfer {
  int packetsOrStreamId();
  @Value PacketFM packets(@Flexible long index);
}

@ForeignInterface
interface Transfers {
  Names submit(Transfer transfer, Names source, NamesFM destination);
}
