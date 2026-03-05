package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public record SampleNoiseRouterPlacementCondition(NoiseRouterTarget target, InclusiveRange<Double> range) implements PlacementCondition {
    public static final MapCodec<SampleNoiseRouterPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NoiseRouterTarget.CODEC.fieldOf("target").forGetter(SampleNoiseRouterPlacementCondition::target),
        Codec.DOUBLE.optionalFieldOf("min_inclusive", Double.MIN_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.DOUBLE.optionalFieldOf("max_inclusive", Double.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleNoiseRouterPlacementCondition::new));
    
    public SampleNoiseRouterPlacementCondition(NoiseRouterTarget target, double minInclusive, double maxInclusive) {
        this(target, new InclusiveRange<>(minInclusive, maxInclusive));
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;

        DensityFunction df = this.target().getDensityFunction(context.randomState().router()).mapAll(new DensityFunctionWrapper(context, chunkGenerator.generatorSettings().value()));
        double density = df.compute(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()));
        
        return this.range.isValueInRange(density);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
