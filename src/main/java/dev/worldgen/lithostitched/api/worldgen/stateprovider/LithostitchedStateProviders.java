package dev.worldgen.lithostitched.api.worldgen.stateprovider;

import net.minecraft.util.random.WeightedList;
import dev.worldgen.lithostitched.worldgen.stateprovider.WeightedProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public interface LithostitchedStateProviders {
	static BlockStateProvider weighted(WeightedList<BlockStateProvider> list) {
		return new WeightedProvider(list);
	}
}
