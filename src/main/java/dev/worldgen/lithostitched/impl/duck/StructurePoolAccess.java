package dev.worldgen.lithostitched.impl.duck;

import dev.worldgen.lithostitched.impl.worldgen.structure.LithostitchedTemplates;

public interface StructurePoolAccess {
    LithostitchedTemplates getLithostitchedTemplates();
    void compileRawTemplates();
}