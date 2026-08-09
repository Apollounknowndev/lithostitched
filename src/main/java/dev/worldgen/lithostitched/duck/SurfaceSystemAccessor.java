package dev.worldgen.lithostitched.duck;

import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface SurfaceSystemAccessor {
    Noise getBandOffsetNoise();
    PositionalRandomFactory getNoiseRandom();
}