package dev.worldgen.lithostitched.access;

import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public interface StructurePoolAccess {
    ShufflingList<StructurePoolElement> getLithostitchedTemplates();
    void setLithostitchedTemplates(ShufflingList<StructurePoolElement> templates);
}