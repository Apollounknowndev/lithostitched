package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class ConditionStructureProcessor extends StructureProcessor {
    public static final Codec<ConditionStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ModifierPredicate.CODEC.fieldOf("predicate").orElse(TrueModifierPredicate.INSTANCE).forGetter(ConditionStructureProcessor::predicate),
        StructureProcessorType.SINGLE_CODEC.fieldOf("processor").forGetter(ConditionStructureProcessor::processor)
    ).apply(instance, ConditionStructureProcessor::new));

    public static final StructureProcessorType<ConditionStructureProcessor> CONDITION_TYPE = () -> CODEC;
    private final ModifierPredicate predicate;
    private final StructureProcessor processor;

    public ConditionStructureProcessor(ModifierPredicate predicate, StructureProcessor processor) {
        this.predicate = predicate;
        this.processor = processor;
    }

    private ModifierPredicate predicate() {
        return this.predicate;
    }
    private StructureProcessor processor() {
        return this.processor;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        if (this.predicate().test()) {
            return this.processor.processBlock(levelReader, blockPos, blockPos2, structureBlockInfo, currentBlockInfo, structurePlaceSettings);
        }

        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return CONDITION_TYPE;
    }
}

