package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.*;

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

        DensityFunction df = this.densityFunction.value().mapAll(new NoiseWiringHelper(context, chunkGenerator.settings.value()));
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
