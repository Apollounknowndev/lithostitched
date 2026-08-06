package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.RandomMode;
import dev.worldgen.lithostitched.platform.LithostitchedVersion;
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

public class SetBlockProcessor implements StructureProcessor {
    public static final MapCodec<SetBlockProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(SetBlockProcessor::blockState),
        Codec.BOOL.fieldOf("preserve_state").orElse(true).forGetter(SetBlockProcessor::preserveState),
        RandomMode.CODEC.fieldOf("random_mode").orElse(RandomMode.PER_BLOCK).forGetter(SetBlockProcessor::randomMode),
        RuleBlockEntityModifier.CODEC.fieldOf("block_entity_modifier").orElse(Passthrough.INSTANCE).forGetter(SetBlockProcessor::modifier)
    ).apply(instance, SetBlockProcessor::new));

    private final BlockStateProvider stateProvider;
    private final boolean preserveState;
    private final RandomMode randomMode;
    private final RuleBlockEntityModifier modifier;

    public SetBlockProcessor(BlockStateProvider stateProvider, boolean preserveState, RandomMode randomMode, RuleBlockEntityModifier modifier) {
        this.stateProvider = stateProvider;
        this.preserveState = preserveState;
        this.randomMode = randomMode;
        this.modifier = modifier;
    }

    public BlockStateProvider blockState() {
        return stateProvider;
    }

    public boolean preserveState() {
        return preserveState;
    }

    public RandomMode randomMode() {
        return randomMode;
    }

    public RuleBlockEntityModifier modifier() {
        return modifier;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
        if (level instanceof WorldGenLevel worldGenLevel) {
            BlockPos samplePos = this.randomMode.select(targetPosition, referencePos, processedBlockInfo);

            RandomSource random = RandomSource.create(worldGenLevel.getSeed()).forkPositional().at(samplePos);
            BlockState state = LithostitchedVersion.getState(this.blockState(), worldGenLevel, random, samplePos);

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
