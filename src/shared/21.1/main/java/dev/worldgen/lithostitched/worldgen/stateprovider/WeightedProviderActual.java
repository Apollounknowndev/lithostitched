package dev.worldgen.lithostitched.worldgen.stateprovider;


import dev.worldgen.lithostitched.api.util.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.msrandom.multiplatform.annotations.Actual;

public class WeightedProviderActual {
	@Actual
	public BlockState getState(RandomSource random, BlockPos pos) {
		WeightedList<BlockStateProvider> providers = ((WeightedProvider)(Object)this).providers();
		return providers.getRandom(random).map(provider ->  provider.getState(random, pos)).orElse(Blocks.AIR.defaultBlockState());
	}
	
	@Actual
	public BlockState getState(WorldGenLevel level, RandomSource random, BlockPos pos) {
		return null;
	}
}
