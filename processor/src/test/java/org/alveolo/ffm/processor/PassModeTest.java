package org.alveolo.ffm.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;

import org.junit.jupiter.api.Test;

class PassModeTest extends AbstractProcessorTest {
  @Test
  void generatesCallPassModes() {
    var compilation = compile("passmode/PassMode.java");

    assertThat(compilation).succeeded();
    assertGenerated(compilation, "passmode.PassModeF",
        "passmode/PassModeF.java");
    assertGenerated(compilation, "passmode.FunctionsF",
        "passmode/FunctionsF.java");
    assertGenerated(compilation, "passmode.VirtualStructVSpec",
        "passmode/VirtualStructVSpec.java");
    assertGenerated(compilation, "passmode.VirtualStructV",
        "passmode/VirtualStructV.java");
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
    assertGenerated(compilation, "passmode.FieldModesF",
        "passmode/FieldModesF.java");
    assertGenerated(compilation, "passmode.FieldModeAccessorsF",
        "passmode/FieldModeAccessorsF.java");
  }
}
