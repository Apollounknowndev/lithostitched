package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public record SampleDensityPlacementCondition(Holder<DensityFunction> densityFunction, InclusiveRange<Double> range) implements PlacementCondition {
    public static final MapCodec<SampleDensityPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("density_function").forGetter(SampleDensityPlacementCondition::densityFunction),
        Codec.DOUBLE.optionalFieldOf("min_inclusive", Double.MIN_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.DOUBLE.optionalFieldOf("max_inclusive", Double.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleDensityPlacementCondition::new));
    
    public SampleDensityPlacementCondition(Holder<DensityFunction> densityFunction, double minInclusive, double maxInclusive) {
        this(densityFunction, new InclusiveRange<>(minInclusive, maxInclusive));
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;

        DensityFunction df = this.densityFunction.value().mapAll(new DensityFunctionWrapper(context, chunkGenerator.generatorSettings().value()));
        double density = df.compute(SimpleContext.of(pos));

        return this.range.isValueInRange(density);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
