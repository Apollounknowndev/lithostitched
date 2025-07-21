package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class BlockSwapStructureProcessor extends StructureProcessor {
    public static final MapCodec<BlockSwapStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("blocks").forGetter(BlockSwapStructureProcessor::blockSwapMap)
    ).apply(instance, BlockSwapStructureProcessor::new));

    public static final StructureProcessorType<BlockSwapStructureProcessor> TYPE = () -> CODEC;
    private final Map<ResourceLocation, ResourceLocation> blockSwapMap;

    public BlockSwapStructureProcessor(Map<ResourceLocation, ResourceLocation> blockSwapMap) {
        this.blockSwapMap = blockSwapMap;
    }

    public Map<ResourceLocation, ResourceLocation> blockSwapMap() {
        return this.blockSwapMap;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        Block oldBlock = currentBlockInfo.state().getBlock();
        ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(oldBlock);
        if (blockSwapMap.containsKey(blockKey)) {
            Optional<Block> newBlock;
            newBlock = BuiltInRegistries.BLOCK.getOptional(blockSwapMap.get(blockKey));
            if (newBlock.isPresent()) {
                return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newBlock.get().withPropertiesOf(currentBlockInfo.state()), currentBlockInfo.nbt());
            }
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}

