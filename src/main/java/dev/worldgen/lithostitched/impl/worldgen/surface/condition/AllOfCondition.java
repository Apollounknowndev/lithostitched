package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public record AllOfCondition(List<SurfaceRules.ConditionSource> conditions) implements SurfaceRules.ConditionSource {
    public static final MapCodec<AllOfCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SurfaceRules.ConditionSource.CODEC.listOf().fieldOf("conditions").forGetter(AllOfCondition::conditions)
    ).apply(instance, AllOfCondition::new));
    public static final KeyDispatchDataCodec<AllOfCondition> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);
    
    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return DATA_CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        return new Condition(this.conditions.stream().map(source -> source.apply(context)).toList());
    }

    private record Condition(List<SurfaceRules.Condition> conditions) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            for (SurfaceRules.Condition condition : this.conditions) {
                if (!condition.test()) return false;
            }
            return true;
        }
    }
}
