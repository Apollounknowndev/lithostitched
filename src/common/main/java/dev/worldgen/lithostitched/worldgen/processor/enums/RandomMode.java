package dev.worldgen.lithostitched.worldgen.processor.enums;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public enum RandomMode implements StringRepresentable {
    PER_BLOCK("per_block"),
    PER_PIECE("per_piece");

    public static final Codec<RandomMode> CODEC = StringRepresentable.fromEnum(RandomMode::values);
    private final String name;

    RandomMode(String name) {
        this.name = name;
    }

    public BlockPos select(BlockPos piecePos, StructureTemplate.StructureBlockInfo blockPos) {
        return this == PER_PIECE ? piecePos : blockPos.pos();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
