package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
        Codec.unboundedMap(ResourceKey.codec(Registries.BLOCK), ResourceKey.codec(Registries.BLOCK)).fieldOf("blocks").forGetter(BlockSwapStructureProcessor::blockSwapMap)
    ).apply(instance, BlockSwapStructureProcessor::new));

    public static final StructureProcessorType<BlockSwapStructureProcessor> TYPE = () -> CODEC;
    private final Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap;

    public BlockSwapStructureProcessor(Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap) {
        this.blockSwapMap = blockSwapMap;
    }

    public Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap() {
        return this.blockSwapMap;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        HolderLookup.RegistryLookup<Block> registry = levelReader.registryAccess().lookupOrThrow(Registries.BLOCK);
        ResourceKey<Block> key = currentBlockInfo.state().getBlock().builtInRegistryHolder().key();
        if (blockSwapMap.containsKey(key)) {
            Optional<Holder.Reference<Block>> newBlock;
            newBlock = registry.get(blockSwapMap.get(key));
            if (newBlock.isPresent()) {
                return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newBlock.get().value().withPropertiesOf(currentBlockInfo.state()), currentBlockInfo.nbt());
            }
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}

