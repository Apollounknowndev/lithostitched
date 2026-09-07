package dev.worldgen.lithostitched.impl.worldgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.MaterialRules.DensityGetter;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public record SampleDensityCondition(DensityFunction function, InclusiveRange<Float> range, boolean prefill) implements MaterialCondition {
    public static final MapCodec<SampleDensityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("function").forGetter(SampleDensityCondition::function),
        LithostitchedCodecs.FLOAT_RANGE.fieldOf("range").forGetter(SampleDensityCondition::range),
        Codec.BOOL.fieldOf("prefill").forGetter(SampleDensityCondition::prefill)
    ).apply(instance, SampleDensityCondition::new));
    
    @Override
    public ConditionEvaluator compile(MaterialRuleContext context) {
        DensityGetter densitySampler = context.getDensitiesInChunk(this.function, this.prefill);
        return () -> range.isValueInRange(densitySampler.get());
    }
    
    @Override
    public MapCodec<? extends MaterialCondition> codec() {
        return CODEC;
    }
}
