package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record Not(ProcessorCondition condition) implements ProcessorCondition {
    public static final MapCodec<Not> CODEC = ProcessorCondition.BASE_CODEC.fieldOf("condition").xmap(Not::new, Not::condition);

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        return !this.condition.test(level, data, settings, random);
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}
