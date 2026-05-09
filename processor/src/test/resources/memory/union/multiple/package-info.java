@ForeignUnion(name="Union", elements={
  @Element(name="i", type=int.class),
  @Element(name="d", type=double.class)
})
@ForeignUnion(name="Union2", elements={
  @Element(name="a", type=long.class)
})
package pkg;
import org.alveolo.ffm.*;
