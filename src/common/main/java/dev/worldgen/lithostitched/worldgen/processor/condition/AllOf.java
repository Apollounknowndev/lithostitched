package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;

public record AllOf(List<ProcessorCondition> conditions) implements ProcessorCondition {
    public static final MapCodec<AllOf> CODEC = ProcessorCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AllOf::new, AllOf::conditions);

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        for (ProcessorCondition condition : this.conditions) {
            if (!condition.test(level, data, settings, random)) return false;
        }
        return true;
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}
