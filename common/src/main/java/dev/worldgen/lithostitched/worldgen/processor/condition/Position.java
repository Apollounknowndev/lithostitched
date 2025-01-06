package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record Position(PosRuleTest predicate, PosAnchor anchor) implements ProcessorCondition {
    public static final MapCodec<Position> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PosRuleTest.CODEC.fieldOf("predicate").forGetter(Position::predicate),
        PosAnchor.CODEC.fieldOf("anchor").orElse(PosAnchor.STRUCTURE_START).forGetter(Position::anchor)
    ).apply(instance, Position::new));

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        return this.predicate.test(data.relative().pos(), data.absolute().pos(), this.anchor.get(data), random);
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }

    public enum PosAnchor implements StringRepresentable {
        STRUCTURE_START("structure_start"),
        PIECE("piece");

        public static final Codec<PosAnchor> CODEC = StringRepresentable.fromEnum(PosAnchor::values);
        private final String name;

        PosAnchor(String name) {
            this.name = name;
        }

        public BlockPos get(ProcessorCondition.Data data) {
            return this == STRUCTURE_START ? data.pivot() : data.absolute().pos().subtract(data.relative().pos());
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
