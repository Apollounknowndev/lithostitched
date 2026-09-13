package dev.worldgen.lithostitched.impl.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.AquiferTarget;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

import java.util.Optional;

public record SampleAquifersPlacementCondition(AquiferTarget target, InclusiveRange<Float> range) implements PlacementCondition {
    public static final MapCodec<SampleAquifersPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        AquiferTarget.CODEC.fieldOf("target").forGetter(SampleAquifersPlacementCondition::target),
        Codec.FLOAT.optionalFieldOf("min_inclusive", -Float.MAX_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.FLOAT.optionalFieldOf("max_inclusive", Float.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleAquifersPlacementCondition::new));
    
    public SampleAquifersPlacementCondition(AquiferTarget target, float minInclusive, float maxInclusive) {
        this(target, new InclusiveRange<>(minInclusive, maxInclusive));
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (!(context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator)) return false;
        
        Optional<Aquifer.Config> aquifers = chunkGenerator.generatorSettings().value().aquifers();
        if (aquifers.isEmpty()) return false;
        
        float density = context
            .randomState()
            .samplersWithContext(SamplerContext.builder().enableCaches().build())
            .sampleValue(this.target.getDensityFunction(aquifers.get()), pos.getX(), pos.getY(), pos.getZ());
        
        return this.range.isValueInRange(density);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
