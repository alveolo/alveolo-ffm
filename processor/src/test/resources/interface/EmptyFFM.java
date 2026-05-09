package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class EmptyFFM implements Empty {
  public static final EmptyFFM INSTANCE = new EmptyFFM();

  private EmptyFFM() {}

  static {
  }

  private static final Linker FF$LINKER = Linker.nativeLinker();

  private static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();
}
