package dev.worldgen.lithostitched.api.worldgen.densityfunction;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.FastNoiseDensityFunction;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.WrappedMarkerDensityFunction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public interface LithostitchedDensityFunctions {
	static DensityFunction fastNoise(Holder<FastNoiseConfig> config, double xzScale, double yScale) {
		return new FastNoiseDensityFunction(config, xzScale, yScale, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
	}
	
	static DensityFunction fastNoise(Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
		return new FastNoiseDensityFunction(config, xzScale, yScale, shiftX, shiftY, shiftZ);
	}
	
	static DensityFunction wrappedMarker() {
		return new WrappedMarkerDensityFunction();
	}
	
	static DensityFunction originalMarker() {
		return new WrappedMarkerDensityFunction();
	}
}
