package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ApplyRandomStructureProcessor extends StructureProcessor {
    private static final Codec<SimpleWeightedRandomList<Holder<StructureProcessorList>>> WEIGHTED_LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(StructureProcessorType.LIST_CODEC);
    private static final Codec<HolderSet<StructureProcessorList>> SET_CODEC = RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC);

    public static final Codec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        LithostitchedCodecs.withAlternative(SET_CODEC, WEIGHTED_LIST_CODEC, ApplyRandomStructureProcessor::convertToSet).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists),
        RandomSettings.CODEC.fieldOf("mode").forGetter(ApplyRandomStructureProcessor::randomSettings)
    ).apply(instance, ApplyRandomStructureProcessor::new));

    private static HolderSet<StructureProcessorList> convertToSet(SimpleWeightedRandomList<Holder<StructureProcessorList>> weightedList) {
        List<Holder<StructureProcessorList>> holders = new ArrayList<>();
        for (WeightedEntry.Wrapper<Holder<StructureProcessorList>> processor : weightedList.unwrap()) {
            for (int i = 0; i < processor.getWeight().asInt(); i++) {
                holders.add(processor.getData());
            }
        }
        return HolderSet.direct(holders);
    }

    public static final StructureProcessorType<ApplyRandomStructureProcessor> TYPE = () -> CODEC;
    private final HolderSet<StructureProcessorList> processorLists;
    private final RandomSettings randomSettings;

    public ApplyRandomStructureProcessor(HolderSet<StructureProcessorList> processorLists, RandomSettings randomSettings) {
        this.processorLists = processorLists;
        this.randomSettings = randomSettings;
    }

    public HolderSet<StructureProcessorList> processorLists() {
        return this.processorLists;
    }

    public RandomSettings randomSettings() {
        return this.randomSettings;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            RandomSource random = this.randomSettings.create(level, pos, absolute);

            if (this.processorLists.size() > 0) {
                var processorList = this.processorLists.get(random.nextInt(this.processorLists.size()));

                StructureTemplate.StructureBlockInfo processedBlock = absolute;

                for (StructureProcessor processor : processorList.value().list()) {
                    processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);

                    if (processedBlock == null) break;
                }

                return processedBlock;
            }
        }

        return absolute;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}
