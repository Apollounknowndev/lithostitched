package dev.worldgen.lithostitched.api.worldgen.processor.enums;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public enum ProcessorPosition implements StringRepresentable {
    BLOCK("block"),
    PIECE("piece"),
    STRUCTURE_START("structure_start");

    public static final Codec<ProcessorPosition> CODEC = StringRepresentable.fromEnum(ProcessorPosition::values);
    private final String name;

    ProcessorPosition(String name) {
        this.name = name;
    }
    
    public BlockPos select(ProcessorCondition.Data data) {
        return select(data.pos(), data.pivot(), data.absolute());
    }
    
    public BlockPos select(BlockPos piecePos, BlockPos pivotPos, StructureBlockInfo processedBlockInfo) {
        return switch (this) {
            case BLOCK -> processedBlockInfo.pos();
            case PIECE -> piecePos;
            case STRUCTURE_START -> pivotPos;
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
