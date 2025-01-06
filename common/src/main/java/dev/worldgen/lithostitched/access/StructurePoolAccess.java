package dev.worldgen.lithostitched.access;

import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;

public interface StructurePoolAccess {
    LithostitchedTemplates getLithostitchedTemplates();
    void compileRawTemplates();
}