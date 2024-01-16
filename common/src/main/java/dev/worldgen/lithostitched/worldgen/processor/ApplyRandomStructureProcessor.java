package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

public class ApplyRandomStructureProcessor extends StructureProcessor {
    public static final Codec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists)
    ).apply(instance, ApplyRandomStructureProcessor::new));

    public static final StructureProcessorType<ApplyRandomStructureProcessor> APPLY_RANDOM_TYPE = () -> CODEC;
    private final HolderSet<StructureProcessorList> processorLists;

    public ApplyRandomStructureProcessor(HolderSet<StructureProcessorList> processorLists) {
        this.processorLists = processorLists;
    }

    public HolderSet<StructureProcessorList> processorLists() {
        return this.processorLists;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        LithostitchedCommon.LOGGER.info(String.valueOf(processorLists.size()));
        Holder<StructureProcessorList> processorList = processorLists.get(structurePlaceSettings.getRandom(currentBlockInfo.pos()).nextInt(processorLists.size()));
        for (StructureProcessor processor : processorList.value().list()) {
            StructureTemplate.StructureBlockInfo candidateBlockInfo = processor.processBlock(levelReader, blockPos, blockPos2, structureBlockInfo, currentBlockInfo, structurePlaceSettings);
            if (candidateBlockInfo != currentBlockInfo) {
                return candidateBlockInfo;
            }
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return APPLY_RANDOM_TYPE;
    }
}
