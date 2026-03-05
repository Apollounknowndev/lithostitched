package dev.worldgen.lithostitched.impl.worldgen.bandlands;

import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import dev.worldgen.lithostitched.api.worldgen.bandlands.BandlandsBuilder;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.BaseBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.RepeatingBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.WrappedBand;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BandlandsBuilderImpl implements BandlandsBuilder {
	private final BlockState base;
	private final List<Band> bands;
	
	public BandlandsBuilderImpl(BlockState base) {
		this.base = base;
		this.bands = new ArrayList<>();
	}
	
	@Override
	public BandlandsBuilder baseBand(IntProvider count, IntProvider size, BlockState state) {
		this.bands.add(new BaseBand(count, size, state));
		return this;
	}
	
	@Override
	public BandlandsBuilder repeatBand(IntProvider interval, IntProvider size, BlockState state) {
		this.bands.add(new RepeatingBand(interval, size, state));
		return this;
	}
	
	@Override
	public BandlandsBuilder wrappedBand(IntProvider interval, IntProvider maxCount, float wrapperChance, BlockState wrapperState, BlockState wrappedState) {
		this.bands.add(new WrappedBand(interval, maxCount, wrapperChance, wrapperState, wrappedState));
		return this;
	}
	
	@Override
	public Bandlands build() {
		return new Bandlands(base, bands);
	}
}
