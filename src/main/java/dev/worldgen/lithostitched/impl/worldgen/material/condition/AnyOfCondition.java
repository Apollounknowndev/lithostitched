package dev.worldgen.lithostitched.impl.worldgen.material.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

import java.util.List;

public record AnyOfCondition(List<MaterialCondition> conditions) implements MaterialCondition {
    public static final MapCodec<AnyOfCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        MaterialCondition.CODEC.listOf().fieldOf("conditions").forGetter(AnyOfCondition::conditions)
    ).apply(instance, AnyOfCondition::new));
    
    @Override
    public MapCodec<? extends MaterialCondition> codec() {
        return CODEC;
    }
    
    @Override
    public ConditionEvaluator compile(MaterialRuleContext context) {
        List<ConditionEvaluator> evaluators = this.conditions.stream().map(condition -> condition.compile(context)).toList();
        return () -> {
            for (ConditionEvaluator evaluator : evaluators) {
                if (evaluator.test()) return true;
            }
            return false;
        };
    }
}
