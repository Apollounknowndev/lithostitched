package dev.worldgen.lithostitched.api.worldgen.densityfunction;

import dev.worldgen.lithostitched.api.worldgen.densityfunction.cellular.ReturnType;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CellularDensityFunction.GridDimensions;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.*;
import net.minecraft.core.Holder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

import java.util.Optional;

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
	
	static DensityFunction cellular2d(int gridSize, float jitter, ReturnType returnType, int salt) {
		return new CellularDensityFunction(Optional.empty(), new GridDimensions(gridSize, 0), jitter, returnType, salt, 0);
	}
	
	static DensityFunction cellular2d(DensityFunction condition, float minValue, float maxValue, float fallback, int gridSize, float jitter, ReturnType returnType, int salt) {
		return new CellularDensityFunction(Optional.of(new CellularDensityFunction.CellCondition(condition, new InclusiveRange<>(minValue, maxValue), fallback)), new GridDimensions(gridSize, 0), jitter, returnType, salt, 0);
	}
	
	static DensityFunction cellular3d(int gridSizeXZ, int gridSizeY, float jitter, ReturnType returnType, int salt) {
		return new CellularDensityFunction(Optional.empty(), new GridDimensions(gridSizeXZ, gridSizeY), jitter, returnType, salt, 0);
	}
	
	static DensityFunction cellular3d(DensityFunction condition, float minValue, float maxValue, float fallback, int gridSizeXZ, int gridSizeY, float jitter, ReturnType returnType, int salt) {
		return new CellularDensityFunction(Optional.of(new CellularDensityFunction.CellCondition(condition, new InclusiveRange<>(minValue, maxValue), fallback)), new GridDimensions(gridSizeXZ, gridSizeY), jitter, returnType, salt, 0);
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
