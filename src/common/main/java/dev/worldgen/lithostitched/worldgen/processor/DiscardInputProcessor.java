package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DiscardInputProcessor extends StructureProcessor {
    public static final DiscardInputProcessor INSTANCE = new DiscardInputProcessor();
    public static final MapCodec<DiscardInputProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final StructureProcessorType<DiscardInputProcessor> TYPE = () -> CODEC;

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        return null;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
