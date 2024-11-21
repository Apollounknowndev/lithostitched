package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record Position(PosRuleTest predicate) implements ProcessorCondition {
    public static final MapCodec<Position> CODEC = PosRuleTest.CODEC.fieldOf("predicate").xmap(Position::new, Position::predicate);

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        return this.predicate.test(data.relative().pos(), data.absolute().pos(), data.pivot(), random);
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}
