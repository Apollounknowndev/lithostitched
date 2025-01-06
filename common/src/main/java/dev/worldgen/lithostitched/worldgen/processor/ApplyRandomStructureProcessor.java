package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

public class ApplyRandomStructureProcessor extends StructureProcessor {
    private static final Codec<SimpleWeightedRandomList<Holder<StructureProcessorList>>> WEIGHTED_LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(StructureProcessorType.LIST_CODEC);
    private static final Codec<HolderSet<StructureProcessorList>> SET_CODEC = RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC);

    public static final MapCodec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.withAlternative(WEIGHTED_LIST_CODEC, SET_CODEC, ApplyRandomStructureProcessor::convertToWeightedList).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists),
        RandomSettings.CODEC.fieldOf("mode").forGetter(ApplyRandomStructureProcessor::randomSettings)
    ).apply(instance, ApplyRandomStructureProcessor::new));

    private static SimpleWeightedRandomList<Holder<StructureProcessorList>> convertToWeightedList(HolderSet<StructureProcessorList> set) {
        var weightedList = SimpleWeightedRandomList.<Holder<StructureProcessorList>>builder();
        for (Holder<StructureProcessorList> processor : set) {
            weightedList.add(processor, 1);
        }
        return weightedList.build();
    }

    public static final StructureProcessorType<ApplyRandomStructureProcessor> TYPE = () -> CODEC;
    private final SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists;
    private final RandomSettings randomSettings;

    public ApplyRandomStructureProcessor(SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists, RandomSettings randomSettings) {
        this.processorLists = processorLists;
        this.randomSettings = randomSettings;
    }

    public ApplyRandomStructureProcessor(HolderSet<StructureProcessorList> set, RandomSettings mode) {
        this(convertToWeightedList(set), mode);
    }

    public SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists() {
        return this.processorLists;
    }

    public RandomSettings randomSettings() {
        return this.randomSettings;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            RandomSource random = this.randomSettings.create(level, pos, absolute);

            var processorList = this.processorLists.getRandomValue(random);
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
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}
