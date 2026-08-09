package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Optional;

public record VinesFeature(WeightedList<Block> blocks, Optional<HolderSet<Block>> canPlaceOn, IntProvider maxLength) implements Feature {
    private static final WeightedList<Block> DEFAULT_BLOCK = WeightedList.<Block>builder().add(Blocks.VINE).build();
    
    public static final MapCodec<VinesFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LithostitchedCodecs.compactWeightedList(BuiltInRegistries.BLOCK.byNameCodec(), false).fieldOf("block").orElse(DEFAULT_BLOCK).forGetter(VinesFeature::blocks),
        LithostitchedCodecs.BLOCK_SET.optionalFieldOf("can_place_on").forGetter(VinesFeature::canPlaceOn),
        IntProviders.codec(1, 256).fieldOf("max_length").orElse(ConstantInt.of(1)).forGetter(VinesFeature::maxLength)
    ).apply(instance, VinesFeature::new));
    
    
    public boolean canPlaceOn(BlockState state) {
        return this.canPlaceOn.isEmpty() || state.is(this.canPlaceOn.get());
    }
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        BlockPos.MutableBlockPos pos = origin.mutable();
        
        var states = this.blocks().getRandom(random);
        
        if (states.isEmpty()) return false;
        
        boolean anyPlaced = false;
        
        for (int i = 0; i < this.maxLength().sample(random); i++) {
            if (!level.isEmptyBlock(pos)) break;
            
            Block vine = states.get();
            
            boolean placed = false;
            
            for (Direction direction : Direction.values()) {
                if (direction == Direction.DOWN) continue;
                
                if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction) && this.canPlaceOn(level.getBlockState(pos.relative(direction)))) {
                    level.setBlock(pos, vine.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), true), 2);
                    placed = true;
                }
                
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.getBlock() instanceof VineBlock) {
                    if (
                        aboveState.getValue(VineBlock.NORTH) ||
                            aboveState.getValue(VineBlock.EAST) ||
                            aboveState.getValue(VineBlock.SOUTH) ||
                            aboveState.getValue(VineBlock.WEST)
                    ) {
                        level.setBlock(pos, vine.withPropertiesOf(aboveState).setValue(VineBlock.UP, false), 2);
                        placed = true;
                        
                    }
                }
            }
            
            if (!placed) break;
            
            anyPlaced = true;
            pos.move(Direction.DOWN);
        }
        
        return anyPlaced;
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}
