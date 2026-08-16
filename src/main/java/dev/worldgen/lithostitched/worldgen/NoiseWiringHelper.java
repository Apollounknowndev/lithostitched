package dev.worldgen.lithostitched.worldgen;

import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deprecated, use {@link DensityFunctionWrapper} instead.
 */
@SuppressWarnings("unused")
@Deprecated(forRemoval = true)
public class NoiseWiringHelper extends DensityFunctionWrapper {
    public NoiseWiringHelper(PlacementCondition.Context context, NoiseGeneratorSettings settings) {
        super(context, settings);
    }
    
    public NoiseWiringHelper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
        super(seed, useLegacySource, randomState, random);
    }
}