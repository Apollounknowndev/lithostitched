package dev.worldgen.lithostitched.duck;

import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;

public interface StructurePoolAccess {
    LithostitchedTemplates getLithostitchedTemplates();
    void compileRawTemplates();
}