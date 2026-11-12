package dev.worldgen.lithostitched.impl.worldgen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PassthroughProvider() implements BlockStateProvider {
    public static final MapCodec<PassthroughProvider> CODEC = MapCodec.unit(PassthroughProvider::new);
    
    @Override
    public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
        return level.getBlockState(pos);
    }
    
    @Override
    public BlockState getOptionalState(LevelAccessor level, RandomSource random, BlockPos pos) {
        return null;
    }
    
    @Override
    public MapCodec<? extends BlockStateProvider> codec() {
        return CODEC;
    }
}
