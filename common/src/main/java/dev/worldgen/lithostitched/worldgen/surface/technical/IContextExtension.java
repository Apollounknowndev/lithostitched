package dev.worldgen.lithostitched.worldgen.surface.technical;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface IContextExtension {
    // Rule return functions for cached LazyXZCondition rules
    SurfaceRules.Condition naturalphilosophy$getCliff();
    SurfaceRules.Condition naturalphilosophy$getFlat();
    SurfaceRules.Condition naturalphilosophy$getFlatLiquid();
    SurfaceRules.Condition naturalphilosophy$getLandTopLayer();
    // Value return functions for cached parameterized rules & conditions
    int naturalphilosophy$getOceanHeightmapDepth();
    double naturalphilosophy$getCachedNoise(ResourceKey<NormalNoise.NoiseParameters> noise);
}
