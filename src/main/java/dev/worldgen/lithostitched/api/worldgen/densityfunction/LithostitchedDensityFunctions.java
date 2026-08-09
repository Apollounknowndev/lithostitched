package dev.worldgen.lithostitched.api.worldgen.densityfunction;

import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public interface LithostitchedDensityFunctions {
	static DensityFunction cos(DensityFunction input) {
		return new CosDensityFunction(input);
	}
	
	static DensityFunction shift(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
		return new ShiftDensityFunction(input, shiftX, shiftY, shiftZ);
	}
	
	static DensityFunction sin(DensityFunction input) {
		return new SinDensityFunction(input);
	}
	
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
		return new OriginalMarkerDensityFunction();
	}
}
