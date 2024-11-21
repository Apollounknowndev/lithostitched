package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.List;

public class ConditionProcessor extends StructureProcessor {
    public static final MapCodec<ConditionProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RandomSettings.CODEC.fieldOf("random_mode").forGetter(ConditionProcessor::randomSettings),
        ProcessorCondition.CODEC.fieldOf("if_true").forGetter(ConditionProcessor::condition),
        LithostitchedCodecs.singleOrList(StructureProcessorType.SINGLE_CODEC).fieldOf("then").forGetter(ConditionProcessor::processors)
    ).apply(instance, ConditionProcessor::new));

    public static final StructureProcessorType<ConditionProcessor> TYPE = () -> CODEC;

    private final RandomSettings randomSettings;
    private final ProcessorCondition condition;
    private final List<StructureProcessor> processors;

    public ConditionProcessor(RandomSettings randomSettings, ProcessorCondition condition, List<StructureProcessor> processors) {
        this.randomSettings = randomSettings;
        this.condition = condition;
        this.processors = processors;
    }

    public RandomSettings randomSettings() {
        return randomSettings;
    }

    public ProcessorCondition condition() {
        return condition;
    }

    private List<StructureProcessor> processors() {
        return this.processors;
    }

    @Override
    public StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            RandomSource random = this.randomSettings.create(level, pos, absolute);
            StructureBlockInfo corrected = new StructureBlockInfo(absolute.pos(), level.getBlockState(absolute.pos()), absolute.nbt());

            if (this.condition.test(level, new ProcessorCondition.Data(pos, pivot, relative, corrected), settings, random)) {
                StructureBlockInfo processedBlock = absolute;

                for (StructureProcessor processor : this.processors) {
                    processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);

                    if (processedBlock == null) break;
                }

                return processedBlock;
            }
        }
        return absolute;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
