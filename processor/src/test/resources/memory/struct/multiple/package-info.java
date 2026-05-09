@ForeignStruct(name="Struct", elements={
  @Element(name="x", type=int.class),
  @Element(name="y", type=int.class),
  @Element(name="s", type=int.class, sequence=5)
})
@ForeignStruct(name="Struct2", elements={
  @Element(name="a", type=long.class)
})
package pkg;
import org.alveolo.ffm.*;
