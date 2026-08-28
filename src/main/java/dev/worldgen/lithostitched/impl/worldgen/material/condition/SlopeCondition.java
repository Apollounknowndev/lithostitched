package dev.worldgen.lithostitched.impl.worldgen.material.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public record SlopeCondition(InclusiveRange<Integer> threshold) implements MaterialCondition {
    private static final InclusiveRange<Integer> BASE_DIFFERENCE = new InclusiveRange<>(4, Integer.MAX_VALUE);
    public static final MapCodec<SlopeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LithostitchedCodecs.INT_RANGE.fieldOf("height_difference").orElse(BASE_DIFFERENCE).forGetter(SlopeCondition::threshold)
    ).apply(instance, SlopeCondition::new));
    
    
    @Override
    public ConditionEvaluator compile(MaterialRuleContext context) {
        return new MaterialRuleContext.LazyXZCondition(context) {
            @Override
            protected boolean compute() {
                return threshold.isValueInRange(Math.abs(this.context.surfaceGradientX())) || threshold.isValueInRange(Math.abs(this.context.surfaceGradientZ()));
            }
        };
    }
    
    @Override
    public MapCodec<? extends MaterialCondition> codec() {
        return CODEC;
    }
}
