package dev.worldgen.lithostitched.impl.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record SampleNoiseRouterPlacementCondition(NoiseRouterTarget target, InclusiveRange<Float> range) implements PlacementCondition {
    public static final MapCodec<SampleNoiseRouterPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NoiseRouterTarget.CODEC.fieldOf("target").forGetter(SampleNoiseRouterPlacementCondition::target),
        Codec.FLOAT.optionalFieldOf("min_inclusive", -Float.MAX_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.FLOAT.optionalFieldOf("max_inclusive", Float.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleNoiseRouterPlacementCondition::new));
    
    public SampleNoiseRouterPlacementCondition(NoiseRouterTarget target, float minInclusive, float maxInclusive) {
        this(target, new InclusiveRange<>(minInclusive, maxInclusive));
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;

        float density = context
            .randomState()
            .samplersWithContext(SamplerContext.builder().enableCaches().build())
            .sampleValue(this.target.getDensityFunction(chunkGenerator.generatorSettings().value().noiseRouter()), pos.getX(), pos.getY(), pos.getZ());
        
        return this.range.isValueInRange(density);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
