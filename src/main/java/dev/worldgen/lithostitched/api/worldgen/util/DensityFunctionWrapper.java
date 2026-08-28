package dev.worldgen.lithostitched.api.worldgen.util;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctionCompiler;
import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DensityFunctionWrapper implements DensityFunction.CompileContext {
    private final boolean useLegacyRandom;
    private final long seed;
    final RandomState randomState;
    final PositionalRandomFactory random;

    public DensityFunctionWrapper(PlacementCondition.Context context, NoiseGeneratorSettings settings) {
        this(context.seed(), settings.useLegacyRandomSource(), context.randomState(), ((RandomStateAccessor)(Object)context.randomState()).getRandom());
    }

    public DensityFunctionWrapper(long seed, boolean useLegacyRandom, RandomState randomState, PositionalRandomFactory random) {
        this.seed = seed;
        this.useLegacyRandom = useLegacyRandom;
        this.randomState = randomState;
        this.random = random;
    }
    
    private RandomSource newLegacyInstance(final long seedOffset) {
        return new LegacyRandomSource(seed + seedOffset);
    }
    
    @Override
    public Noise createNoiseSampler(final Holder<NormalNoise> parameters) {
        if (parameters.is(Noises.TEMPERATURE_NETHER)) {
            return parameters.value().createForLegacyNetherBiome(this.newLegacyInstance(0L));
        } else {
            return parameters.is(Noises.VEGETATION_NETHER) ? parameters.value().createForLegacyNetherBiome(this.newLegacyInstance(1L)) : this.randomState.getOrCreateNoise(parameters.unwrapKey().orElseThrow());
        }
    }
    
    @Override
    public RandomSource createRandom(final Identifier seed) {
        return useLegacyRandom && seed.equals(BlendedNoise.NOISE_SEED) ? this.newLegacyInstance(0L) : this.random.fromHashOf(seed);
    }
    
    @Override
    public RandomSource createEndIslandRandom() {
        return new LegacyRandomSource(seed);
    }
}