package pkg;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@javax.annotation.processing.Generated(
    "org.alveolo.ffm.processor.ForeignInterfaceProcessor")
public final class RenamedLibraryFFM implements LibraryApi {
  public static final RenamedLibraryFFM INSTANCE = new RenamedLibraryFFM();

  private RenamedLibraryFFM() {}

  public static final Linker FF$LINKER = Linker.nativeLinker();

  public static final SymbolLookup FF$LOOKUP = FF$LINKER.defaultLookup();
}
