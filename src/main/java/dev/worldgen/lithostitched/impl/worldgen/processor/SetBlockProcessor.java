package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.RandomMode;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

public record SetBlockProcessor(Holder<BlockStateProvider> stateProvider, boolean preserveState, RandomMode randomMode, RuleBlockEntityModifier modifier) implements StructureProcessor {
    public static final MapCodec<SetBlockProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(SetBlockProcessor::stateProvider),
        Codec.BOOL.fieldOf("preserve_state").orElse(true).forGetter(SetBlockProcessor::preserveState),
        RandomMode.CODEC.fieldOf("random_mode").orElse(RandomMode.PER_BLOCK).forGetter(SetBlockProcessor::randomMode),
        RuleBlockEntityModifier.CODEC.fieldOf("block_entity_modifier").orElse(Passthrough.INSTANCE).forGetter(SetBlockProcessor::modifier)
    ).apply(instance, SetBlockProcessor::new));
    
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
        if (level instanceof WorldGenLevel worldGenLevel) {
            BlockPos samplePos = this.randomMode.select(targetPosition, referencePos, processedBlockInfo);
            
            RandomSource random = RandomSource.create(worldGenLevel.getSeed()).forkPositional().at(samplePos);
            BlockState state = this.stateProvider().value().getState(worldGenLevel, random, samplePos);
            
            if (this.preserveState) {
                return withState(random, processedBlockInfo, state.getBlock().withPropertiesOf(processedBlockInfo.state()));
            }
            return withState(random, processedBlockInfo, state);
        }
        return processedBlockInfo;
    }
    
    private StructureTemplate.StructureBlockInfo withState(RandomSource random, StructureTemplate.StructureBlockInfo info, BlockState state) {
        return new StructureTemplate.StructureBlockInfo(info.pos(), state, this.modifier.apply(random, info.nbt()));
    }
    
    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
