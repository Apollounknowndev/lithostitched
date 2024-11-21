package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record RandomChance(float chance) implements ProcessorCondition {
    public static final MapCodec<RandomChance> CODEC = Codec.floatRange(0, 1).fieldOf("chance").xmap(RandomChance::new, RandomChance::chance);

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        return random.nextFloat() < this.chance;
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}
