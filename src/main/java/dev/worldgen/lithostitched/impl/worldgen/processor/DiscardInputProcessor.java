package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DiscardInputProcessor implements StructureProcessor {
    public static final DiscardInputProcessor INSTANCE = new DiscardInputProcessor();
    public static final MapCodec<DiscardInputProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, BlockPos relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        return null;
    }
    
    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
