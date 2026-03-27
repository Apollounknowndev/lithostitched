package dev.worldgen.lithostitched.api.worldgen.densityfunction;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.FastNoiseDensityFunction;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.WrappedMarkerDensityFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public interface LithostitchedDensityFunctions {
	static DensityFunction axis(Direction.Axis axis) {
		return new AxisDensityFunction(axis);
	}
	
	static DensityFunction ceil(DensityFunction input) {
		return new CeilDensityFunction(input);
	}
	
	static DensityFunction cos(DensityFunction input) {
		return new CosDensityFunction(input);
	}
	
	static DensityFunction floor(DensityFunction input) {
		return new FloorDensityFunction(input);
	}
	
	static DensityFunction mix(DensityFunction input, DensityFunction argument1, DensityFunction argument2) {
		return new MixDensityFunction(input, argument1, argument2);
	}
	
	static DensityFunction shift(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
		return new ShiftDensityFunction(input, shiftX, shiftY, shiftZ);
	}
	
	static DensityFunction sin(DensityFunction input) {
		return new SinDensityFunction(input);
	}
	
	
	static DensityFunction sqrt(DensityFunction input) {
		return new SqrtDensityFunction(input);
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
