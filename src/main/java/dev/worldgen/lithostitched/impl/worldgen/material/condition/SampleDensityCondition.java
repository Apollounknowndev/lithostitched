package dev.worldgen.lithostitched.impl.worldgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.MaterialRules.DensityGetter;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public record SampleDensityCondition(DensityFunction densityFunction, InclusiveRange<Float> range) implements MaterialCondition {
    public static final MapCodec<SampleDensityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("density_function").forGetter(SampleDensityCondition::densityFunction),
        Codec.FLOAT.optionalFieldOf("min_inclusive", -Float.MAX_VALUE).forGetter(condition -> condition.range.minInclusive()),
        Codec.FLOAT.optionalFieldOf("max_inclusive", Float.MAX_VALUE).forGetter(condition -> condition.range.maxInclusive())
    ).apply(instance, SampleDensityCondition::new));
    
    public SampleDensityCondition(DensityFunction densityFunction, float minInclusive, float maxInclusive) {
        this(densityFunction, new InclusiveRange<>(minInclusive, maxInclusive));
    }
    
    @Override
    public ConditionEvaluator compile(MaterialRuleContext context) {
        DensityGetter densitySampler = context.getDensitiesInChunk(this.densityFunction, true);
        return () -> range.isValueInRange(densitySampler.get());
    }
    
    @Override
    public MapCodec<? extends MaterialCondition> codec() {
        return CODEC;
    }
}
