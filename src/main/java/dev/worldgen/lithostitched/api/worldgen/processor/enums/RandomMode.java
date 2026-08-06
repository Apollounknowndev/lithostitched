package dev.worldgen.lithostitched.api.worldgen.processor.enums;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public enum RandomMode implements StringRepresentable {
    PER_BLOCK("per_block"),
    PER_PIECE("per_piece"),
    PER_STRUCTURE("per_structure");

    public static final Codec<RandomMode> CODEC = StringRepresentable.fromEnum(RandomMode::values);
    private final String name;

    RandomMode(String name) {
        this.name = name;
    }
    
    public BlockPos select(BlockPos piecePos, BlockPos pivotPos, StructureBlockInfo processedBlockInfo) {
        return switch (this) {
            case PER_BLOCK -> processedBlockInfo.pos();
            case PER_PIECE -> piecePos;
            case PER_STRUCTURE -> pivotPos;
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
