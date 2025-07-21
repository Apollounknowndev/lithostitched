package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.MapCodec;
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
    public static final MapCodec<ReferenceStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC).fieldOf("processor_lists").forGetter(ReferenceStructureProcessor::processorLists)
    ).apply(instance, ReferenceStructureProcessor::new));

    public static final StructureProcessorType<ReferenceStructureProcessor> TYPE = () -> CODEC;
    private final HolderSet<StructureProcessorList> processorLists;

    public ReferenceStructureProcessor(HolderSet<StructureProcessorList> processorLists) {
        this.processorLists = processorLists;
    }

    public HolderSet<StructureProcessorList> processorLists() {
        return this.processorLists;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        StructureTemplate.StructureBlockInfo processedBlock = absolute;

        for (Holder<StructureProcessorList> processorList : this.processorLists) {
            for (StructureProcessor processor : processorList.value().list()) {
                processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);

                if (processedBlock == null) return null;
            }
        }

        return processedBlock;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}
