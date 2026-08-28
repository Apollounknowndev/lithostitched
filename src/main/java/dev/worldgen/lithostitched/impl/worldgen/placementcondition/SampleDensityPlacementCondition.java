package dev.worldgen.lithostitched.impl.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record SampleDensityPlacementCondition(DensityFunction densityFunction, InclusiveRange<Float> range) implements PlacementCondition {
    public static final MapCodec<SampleDensityPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("density_function").forGetter(SampleDensityPlacementCondition::densityFunction),
        Codec.FLOAT.optionalFieldOf("min_inclusive", -Float.MAX_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.FLOAT.optionalFieldOf("max_inclusive", Float.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleDensityPlacementCondition::new));
    
    public SampleDensityPlacementCondition(DensityFunction densityFunction, float minInclusive, float maxInclusive) {
        this(densityFunction, new InclusiveRange<>(minInclusive, maxInclusive));
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator)) return false;

        float density = context
            .randomState()
            .samplersWithContext(SamplerContext.builder().enableCaches().build())
            .sampleValue(this.densityFunction, pos.getX(), pos.getY(), pos.getZ());
        return this.range.isValueInRange(density);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
