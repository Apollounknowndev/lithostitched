package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record SampleDensityPlacementCondition(Holder<DensityFunction> densityFunction, Optional<Double> minInclusive, Optional<Double> maxInclusive) implements PlacementCondition {
    public static final MapCodec<SampleDensityPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("density_function").forGetter(SampleDensityPlacementCondition::densityFunction),
        Codec.DOUBLE.optionalFieldOf("min_inclusive").forGetter(SampleDensityPlacementCondition::minInclusive),
        Codec.DOUBLE.optionalFieldOf("max_inclusive").forGetter(SampleDensityPlacementCondition::maxInclusive)
    ).apply(instance, SampleDensityPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;

        DensityFunction df = this.densityFunction.value().mapAll(new NoiseWiringHelper(context.seed(), chunkGenerator.settings.value().useLegacyRandomSource(), context.randomState(), ((RandomStateAccessor)(Object)context.randomState()).getRandom()));
        double density = df.compute(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()));

        boolean min = this.minInclusive.isEmpty() || density >= this.minInclusive.get();
        boolean max = this.maxInclusive.isEmpty() || density <= this.maxInclusive.get();
        return min && max;
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }

    private static class NoiseWiringHelper implements DensityFunction.Visitor {
        private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();
        private final boolean useLegacySource;
        private final long seed;
        final RandomState randomState;
        final PositionalRandomFactory random;
        private RandomSource newLegacyInstance(long noiseSeed) {
            return new LegacyRandomSource(this.seed + noiseSeed);
        }

        NoiseWiringHelper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
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
                    noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                    return new DensityFunction.NoiseHolder(noiseData, noise);
                }

                if (noiseData.is(Noises.VEGETATION)) {
                    noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                    return new DensityFunction.NoiseHolder(noiseData, noise);
                }

                if (noiseData.is(Noises.SHIFT)) {
                    noise = NormalNoise.create(this.random.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0, new double[0]));
                    return new DensityFunction.NoiseHolder(noiseData, noise);
                }
            }

            noise = this.randomState.getOrCreateNoise(noiseData.unwrapKey().orElseThrow());
            return new DensityFunction.NoiseHolder(noiseData, noise);
        }

        private DensityFunction wrapNew(DensityFunction densityFunction) {
            if (densityFunction instanceof BlendedNoise $$1) {
                RandomSource $$2x = this.useLegacySource ? this.newLegacyInstance(0L) : this.random.fromHashOf(ResourceLocation.withDefaultNamespace("terrain"));
                return $$1.withNewRandom($$2x);
            } else {
                return (densityFunction instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(this.seed) : densityFunction);
            }
        }

        public DensityFunction apply(DensityFunction densityFunction) {
            return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
        }
    }
}
