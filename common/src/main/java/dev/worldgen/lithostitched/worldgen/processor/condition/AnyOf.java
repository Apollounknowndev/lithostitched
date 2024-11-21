package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;

public record AnyOf(List<ProcessorCondition> conditions) implements ProcessorCondition {
    public static final MapCodec<AnyOf> CODEC = ProcessorCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AnyOf::new, AnyOf::conditions);

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        for (ProcessorCondition condition : this.conditions) {
            if (condition.test(level, data, settings, random)) return true;
        }
        return false;
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}
