package dev.worldgen.lithostitched.api.worldgen.densityfunction;

import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public interface LithostitchedDensityFunctions {
	static DensityFunction cos(DensityFunction input) {
		return new CosFunction(input);
	}
	
	static DensityFunction shift(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
		return new ShiftFunction(input, shiftX, shiftY, shiftZ);
	}
	
	static DensityFunction sin(DensityFunction input) {
		return new SinFunction(input);
	}
	
	static DensityFunction fastNoise(Holder<FastNoiseConfig> config, double xzScale, double yScale) {
		return new FastNoiseFunction(config, xzScale, yScale, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
	}
	
	static DensityFunction fastNoise(Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
		return new FastNoiseFunction(config, xzScale, yScale, shiftX, shiftY, shiftZ);
	}
	
	static DensityFunction wrappedMarker() {
		return new WrappedMarkerDensityFunction();
	}
	
	static DensityFunction originalMarker() {
		return new OriginalMarkerDensityFunction();
	}
}
