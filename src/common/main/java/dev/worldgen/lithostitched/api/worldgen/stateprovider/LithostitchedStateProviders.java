package dev.worldgen.lithostitched.api.worldgen.stateprovider;

import dev.worldgen.lithostitched.api.util.WeightedList;
import dev.worldgen.lithostitched.worldgen.stateprovider.RandomBlockProvider;
import dev.worldgen.lithostitched.worldgen.stateprovider.WeightedProvider;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Arrays;

public interface LithostitchedStateProviders {
	static BlockStateProvider randomBlock(Block... blocks) {
		return new RandomBlockProvider(HolderSet.direct(Arrays.asList(blocks).stream().map(Block::builtInRegistryHolder).toList()));
	}
	
	static BlockStateProvider randomBlock(HolderSet<Block> blocks) {
		return new RandomBlockProvider(blocks);
	}
	
	static BlockStateProvider weighted(WeightedList<BlockStateProvider> list) {
		return new WeightedProvider(list);
	}
}
