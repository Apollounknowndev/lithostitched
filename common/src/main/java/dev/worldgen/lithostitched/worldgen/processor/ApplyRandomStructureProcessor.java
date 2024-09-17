package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

public class ApplyRandomStructureProcessor extends StructureProcessor {
    public static final MapCodec<ApplyRandomStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SimpleWeightedRandomList.wrappedCodec(StructureProcessorType.LIST_CODEC).fieldOf("processor_lists").forGetter(ApplyRandomStructureProcessor::processorLists),
        Mode.CODEC.fieldOf("mode").forGetter(ApplyRandomStructureProcessor::mode)
    ).apply(instance, ApplyRandomStructureProcessor::new));

    public static final StructureProcessorType<ApplyRandomStructureProcessor> TYPE = () -> CODEC;
    private final SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists;
    private final Mode mode;

    public ApplyRandomStructureProcessor(SimpleWeightedRandomList<Holder<StructureProcessorList>> processorLists, Mode mode) {
        this.processorLists = processorLists;
        this.mode = mode;
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
        Holder<StructureProcessorList> processorList = processorLists.getRandomValue(structurePlaceSettings.getRandom(randomPos)).get();
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
