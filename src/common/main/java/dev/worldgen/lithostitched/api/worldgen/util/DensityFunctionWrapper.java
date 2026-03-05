package dev.worldgen.lithostitched.api.worldgen.util;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DensityFunctionWrapper implements DensityFunction.Visitor {
    private final Map<DensityFunction, DensityFunction> wrapped = new ConcurrentHashMap<>();
    private final boolean useLegacySource;
    private final long seed;
    final RandomState randomState;
    final PositionalRandomFactory random;

    public DensityFunctionWrapper(PlacementCondition.Context context, NoiseGeneratorSettings settings) {
        this(context.seed(), settings.useLegacyRandomSource(), context.randomState(), ((RandomStateAccessor)(Object)context.randomState()).getRandom());
    }

    public DensityFunctionWrapper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
        this.seed = seed;
        this.useLegacySource = useLegacySource;
        this.randomState = randomState;
        this.random = random;
    }

    public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
        Holder<NormalNoise.NoiseParameters> noiseData = noiseHolder.noiseData();
        NormalNoise noise;
        if (this.useLegacySource) {
            if (noiseData.is(Noises.TEMPERATURE)) {
                noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }

            if (noiseData.is(Noises.VEGETATION)) {
                noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }

            if (noiseData.is(Noises.SHIFT)) {
                noise = NormalNoise.create(this.random.fromHashOf(Noises.SHIFT.identifier()), new NormalNoise.NoiseParameters(0, 0.0));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }
        }

        noise = this.randomState.getOrCreateNoise(noiseData.unwrapKey().orElseThrow());
        return new DensityFunction.NoiseHolder(noiseData, noise);
    }

    public DensityFunction apply(DensityFunction densityFunction) {
        return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
    }

    private DensityFunction wrapNew(DensityFunction densityFunction) {
        if (densityFunction instanceof BlendedNoise noise) {
            RandomSource random = this.useLegacySource ? this.newLegacyInstance(0L) : this.random.fromHashOf(Identifier.withDefaultNamespace("terrain"));
            return noise.withNewRandom(random);
        } else {
            return (densityFunction instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(this.seed) : densityFunction);
        }
    }

    private RandomSource newLegacyInstance(long noiseSeed) {
        return new LegacyRandomSource(this.seed + noiseSeed);
    }
}