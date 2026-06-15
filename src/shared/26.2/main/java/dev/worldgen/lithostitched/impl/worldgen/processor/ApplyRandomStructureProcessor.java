package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.util.WeightedHolderSet;
import dev.worldgen.lithostitched.api.worldgen.processor.RandomSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

public record ApplyRandomStructureProcessor(WeightedHolderSet<StructureProcessorList> processorLists, RandomSettings randomSettings) implements StructureProcessor {
    private static final Codec<HolderSet<StructureProcessorList>> SET_CODEC = RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC);
    
    public static final MapCodec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        WeightedHolderSet.codec(SET_CODEC, StructureProcessorType.LIST_CODEC).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists),
        RandomSettings.CODEC.fieldOf("mode").forGetter(ApplyRandomStructureProcessor::randomSettings)
    ).apply(instance, ApplyRandomStructureProcessor::new));
    
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, BlockPos relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            RandomSource random = this.randomSettings.create(level, pos, pivot, absolute);
            
            var processorList = this.processorLists.getRandom(random);
            if (processorList.isPresent()) {
                StructureTemplate.StructureBlockInfo processedBlock = absolute;
                
                for (StructureProcessor processor : processorList.get().value().list()) {
                    processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);
                    
                    if (processedBlock == null) break;
                }
                
                return processedBlock;
            }
        }
        
        return absolute;
    }
    
    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
