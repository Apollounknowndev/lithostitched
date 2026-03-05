package dev.worldgen.lithostitched.api.worldgen.bandlands;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.BandlandsBuilderImpl;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public interface BandlandsBuilder {
	static BandlandsBuilder create(BlockState base) {
		return new BandlandsBuilderImpl(base);
	}
	
	default BandlandsBuilder baseBand(BlockState state) {
		return this.baseBand(UniformInt.of(6, 15), UniformInt.of(1, 3), state);
	}
	BandlandsBuilder baseBand(IntProvider count, IntProvider size, BlockState state);
	BandlandsBuilder repeatBand(IntProvider interval, IntProvider size, BlockState state);
	BandlandsBuilder wrappedBand(IntProvider interval, IntProvider maxCount, float wrapperChance, BlockState wrapperState, BlockState wrappedState);
	
	Bandlands build();
}
