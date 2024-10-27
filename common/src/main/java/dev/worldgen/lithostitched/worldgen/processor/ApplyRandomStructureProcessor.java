package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ApplyRandomStructureProcessor extends StructureProcessor {
    private static final Codec<SimpleWeightedRandomList<Holder<StructureProcessorList>>> WEIGHTED_LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(StructureProcessorType.LIST_CODEC);
    private static final Codec<HolderSet<StructureProcessorList>> SET_CODEC = RegistryCodecs.homogeneousList(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC);

    public static final Codec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.either(WEIGHTED_LIST_CODEC, SET_CODEC).xmap(either -> either.map(Function.identity(), ApplyRandomStructureProcessor::convertToWeightedList), Either::left).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists),
        Mode.CODEC.fieldOf("mode").forGetter(ApplyRandomStructureProcessor::mode)
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
    private final Mode mode;

    public ApplyRandomStructureProcessor(SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists, Mode mode) {
        this.processorLists = processorLists;
        this.mode = mode;
    }

    public ApplyRandomStructureProcessor(HolderSet<StructureProcessorList> set, Mode mode) {
        this(convertToWeightedList(set), mode);
    }

    public SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists() {
        return this.processorLists;
    }

    public Mode mode() {
        return this.mode;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        BlockPos randomPos = this.mode == Mode.PER_BLOCK ? currentBlockInfo.pos() : blockPos;
        var processorList = this.processorLists.getRandomValue(structurePlaceSettings.getRandom(randomPos));
        if (processorList.isPresent()) {
            for (StructureProcessor processor : processorList.get().value().list()) {
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
        return TYPE;
    }

    public enum Mode implements StringRepresentable {
        PER_BLOCK("per_block"),
        PER_PIECE("per_piece");

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
