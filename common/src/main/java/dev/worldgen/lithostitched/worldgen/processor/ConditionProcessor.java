package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import dev.worldgen.lithostitched.worldgen.processor.enums.RandomMode;
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
    private static final Codec<List<StructureProcessor>> PROCESSOR_CODEC = LithostitchedCodecs.compactList(StructureProcessorType.SINGLE_CODEC);
    public static final MapCodec<ConditionProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RandomSettings.CODEC.fieldOf("random_mode").orElse(new RandomSettings(RandomMode.PER_BLOCK)).forGetter(ConditionProcessor::randomSettings),
        ProcessorCondition.CODEC.fieldOf("if_true").forGetter(ConditionProcessor::condition),
        PROCESSOR_CODEC.fieldOf("then").forGetter(ConditionProcessor::thenRun),
        PROCESSOR_CODEC.fieldOf("else").orElse(List.of()).forGetter(ConditionProcessor::elseRun)
    ).apply(instance, ConditionProcessor::new));

    public static final StructureProcessorType<ConditionProcessor> TYPE = () -> CODEC;

    private final RandomSettings randomSettings;
    private final ProcessorCondition condition;
    private final List<StructureProcessor> thenRun;
    private final List<StructureProcessor> elseRun;

    public ConditionProcessor(RandomSettings randomSettings, ProcessorCondition condition, List<StructureProcessor> thenRun, List<StructureProcessor> elseRun) {
        this.randomSettings = randomSettings;
        this.condition = condition;
        this.thenRun = thenRun;
        this.elseRun = elseRun;
    }

    public RandomSettings randomSettings() {
        return randomSettings;
    }

    public ProcessorCondition condition() {
        return condition;
    }

    private List<StructureProcessor> thenRun() {
        return this.thenRun;
    }

    private List<StructureProcessor> elseRun() {
        return this.elseRun;
    }

    @Override
    public StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            RandomSource random = this.randomSettings.create(level, pos, absolute);
            StructureBlockInfo newInput = new StructureBlockInfo(relative.pos(), absolute.state(), absolute.nbt());
            StructureBlockInfo newLocation = new StructureBlockInfo(absolute.pos(), level.getBlockState(absolute.pos()), absolute.nbt());

            boolean passed = this.condition.test(level, new ProcessorCondition.Data(pos, pivot, newInput, newLocation), settings, random);

            StructureBlockInfo processedBlock = absolute;

            for (StructureProcessor processor : passed ? this.thenRun : this.elseRun) {
                processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);

                if (processedBlock == null) break;
            }

            return processedBlock;
        }
        return absolute;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
