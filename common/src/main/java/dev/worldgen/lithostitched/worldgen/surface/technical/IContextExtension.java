package dev.worldgen.lithostitched.worldgen.surface.technical;

import net.minecraft.world.level.levelgen.SurfaceRules;

public interface IContextExtension {
    // Rule return functions for cached LazyXZCondition rules
    SurfaceRules.Condition lithostitched$getCliff();
    SurfaceRules.Condition lithostitched$getFlat();
    SurfaceRules.Condition lithostitched$getFlatLiquid();
    SurfaceRules.Condition lithostitched$getLandTopLayer();
    // Value return functions for cached parameterized rules & conditions
    int lithostitched$getOceanHeightmapDepth();
}
