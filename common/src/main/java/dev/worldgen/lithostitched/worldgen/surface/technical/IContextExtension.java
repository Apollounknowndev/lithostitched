package dev.worldgen.lithostitched.worldgen.surface.technical;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface IContextExtension {
    // Rule return functions for cached LazyXZCondition rules
    SurfaceRules.Condition lithostitched$getCliff();
    SurfaceRules.Condition lithostitched$getFlat();
    SurfaceRules.Condition lithostitched$getFlatLiquid();
    SurfaceRules.Condition lithostitched$getLandTopLayer();
    // Value return functions for cached parameterized rules & conditions
    int lithostitched$getOceanHeightmapDepth();
    double lithostitched$getCachedNoise(ResourceKey<NormalNoise.NoiseParameters> noise);
}
