package dev.worldgen.lithostitched.worldgen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.random.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record WeightedProvider(WeightedList<BlockStateProvider> providers) implements BlockStateProvider {
    public static final MapCodec<WeightedProvider> CODEC = WeightedList.codec(BlockStateProvider.CODEC).fieldOf("providers").xmap(WeightedProvider::new, WeightedProvider::providers);
    
    @Override
    public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
        WeightedList<BlockStateProvider> providers = this.providers();
        return providers.getRandom(random).map(provider ->  provider.getState(level, random, pos)).orElse(Blocks.AIR.defaultBlockState());
    }
    
    @Override
    public MapCodec<? extends BlockStateProvider> codec() {
        return CODEC;
    }
}
