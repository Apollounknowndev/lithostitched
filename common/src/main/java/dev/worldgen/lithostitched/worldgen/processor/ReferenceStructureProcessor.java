package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

public class ReferenceStructureProcessor extends StructureProcessor {
    public static final Codec<ReferenceStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC).fieldOf("processor_lists").forGetter(ReferenceStructureProcessor::processorLists)
    ).apply(instance, ReferenceStructureProcessor::new));

    public static final StructureProcessorType<ReferenceStructureProcessor> REFERENCE_TYPE = () -> CODEC;
    private final HolderSet<StructureProcessorList> processorLists;

    public ReferenceStructureProcessor(HolderSet<StructureProcessorList> processorLists) {
        this.processorLists = processorLists;
    }

    public HolderSet<StructureProcessorList> processorLists() {
        return this.processorLists;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        for (Holder<StructureProcessorList> processorList : processorLists) {
            for (StructureProcessor processor : processorList.value().list()) {
                StructureTemplate.StructureBlockInfo candidateBlockInfo = processor.processBlock(levelReader, blockPos, blockPos2, structureBlockInfo, currentBlockInfo, structurePlaceSettings);
                if (candidateBlockInfo != currentBlockInfo) {
                    return candidateBlockInfo;
                }
            }
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return REFERENCE_TYPE;
    }
}
