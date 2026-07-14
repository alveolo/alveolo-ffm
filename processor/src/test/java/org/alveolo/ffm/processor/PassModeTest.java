package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;

import org.junit.jupiter.api.Test;

class PassModeTest extends AbstractProcessorTest {
  @Test
  void generatesCallPassModes() {
    var compilation = compile("passmode/PassMode.java");

    assertThat(compilation).succeeded();
    assertGenerated(compilation, "passmode.PassModeFFM",
        "passmode/PassModeFFM.java");
    assertGenerated(compilation, "passmode.FunctionsFD",
        "passmode/FunctionsFD.java");
    assertGenerated(compilation, "passmode.VirtualStructVtblSpec",
        "passmode/VirtualStructVtblSpec.java");
    assertGenerated(compilation, "passmode.VirtualStructVtbl",
        "passmode/VirtualStructVtbl.java");
    assertGenerated(compilation, "passmode.CircularDefault",
        "passmode/CircularDefault.java");
    assertGenerated(compilation, "passmode.CircularValue",
        "passmode/CircularValue.java");
    assertGenerated(compilation, "passmode.CircularAddress",
        "passmode/CircularAddress.java");
  }

  @Test
  void generatesFieldPassModes() {
    var compilation = compile("passmode/FieldModes.java");

    assertThat(compilation).succeeded();
    assertGenerated(compilation, "passmode.FieldModesFM",
        "passmode/FieldModesFM.java");
    assertGenerated(compilation, "passmode.FieldModeAccessorsFM",
        "passmode/FieldModeAccessorsFM.java");
  }
}
