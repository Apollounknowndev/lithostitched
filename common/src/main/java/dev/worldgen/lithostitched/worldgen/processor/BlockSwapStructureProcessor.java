package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockSwapStructureProcessor extends StructureProcessor {

    public static final Codec<BlockSwapStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("blocks").forGetter(BlockSwapStructureProcessor::blockSwapMap)
    ).apply(instance, BlockSwapStructureProcessor::new));

    public static final StructureProcessorType<BlockSwapStructureProcessor> BLOCK_SWAP_TYPE = () -> CODEC;
    private final Map<ResourceLocation, ResourceLocation> blockSwapMap;

    public BlockSwapStructureProcessor(Map<ResourceLocation, ResourceLocation> blockSwapMap) {
        this.blockSwapMap = blockSwapMap;
    }

    public Map<ResourceLocation, ResourceLocation> blockSwapMap() {
        return this.blockSwapMap;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        Block block = currentBlockInfo.state().getBlock();
        ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(block);
        if (blockSwapMap.containsKey(blockKey)) {
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), BuiltInRegistries.BLOCK.get(blockSwapMap.get(blockKey)).withPropertiesOf(currentBlockInfo.state()), currentBlockInfo.nbt());
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return BLOCK_SWAP_TYPE;
    }
}

