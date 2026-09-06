package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.duck.SeedAccessor;
import dev.worldgen.lithostitched.mixin.common.NoiseChunkAccessor;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.*;

public record SampleDensityCondition(DensityFunction densityFunction, InclusiveRange<Double> range) implements SurfaceRules.ConditionSource {
    public static final MapCodec<SampleDensityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("density_function").forGetter(SampleDensityCondition::densityFunction),
        Codec.DOUBLE.optionalFieldOf("min_inclusive", Double.MIN_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.DOUBLE.optionalFieldOf("max_inclusive", Double.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleDensityCondition::new));
    
    public SampleDensityCondition(DensityFunction densityFunction, double minInclusive, double maxInclusive) {
        this(densityFunction, new InclusiveRange<>(minInclusive, maxInclusive));
    }
    
    @Override
    public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        ContextAccessor accessor = (ContextAccessor)(Object)context;
        NoiseChunk noiseChunk = accessor.lithostitched$getNoiseChunk();
        RandomState randomState = accessor.lithostitched$getRandomState();
        long seed = ((SeedAccessor)(Object)randomState).getSeed();
        DensityFunctionWrapper wrapper = new DensityFunctionWrapper(seed, false, randomState, ((RandomStateAccessor)(Object)randomState).getRandom());
        DensityFunction df = this.densityFunction
            .mapAll(wrapper)
            .mapAll(((NoiseChunkAccessor)noiseChunk)::lithostitched$wrap);
        
        return () -> this.range.isValueInRange(df.compute(SimpleContext.of(accessor.getPos())));
    }
}
