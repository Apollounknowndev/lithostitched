package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

import java.util.Optional;

public record SampleNoiseRouterPlacementCondition(NoiseRouterTarget target, Optional<Double> minInclusive, Optional<Double> maxInclusive) implements PlacementCondition {
    public static final MapCodec<SampleNoiseRouterPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NoiseRouterTarget.CODEC.fieldOf("target").forGetter(SampleNoiseRouterPlacementCondition::target),
        Codec.DOUBLE.optionalFieldOf("min_inclusive").forGetter(SampleNoiseRouterPlacementCondition::minInclusive),
        Codec.DOUBLE.optionalFieldOf("max_inclusive").forGetter(SampleNoiseRouterPlacementCondition::maxInclusive)
    ).apply(instance, SampleNoiseRouterPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;

        DensityFunction df = this.target().getDensityFunction(context.randomState().router()).mapAll(new NoiseWiringHelper(context, chunkGenerator.settings.value()));
        double density = df.compute(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()));
        boolean min = this.minInclusive.isEmpty() || density >= this.minInclusive.get();
        boolean max = this.maxInclusive.isEmpty() || density <= this.maxInclusive.get();
        return min && max;
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
