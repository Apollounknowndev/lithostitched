package dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.CellularNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.PerlinNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.SimplexNoiseType;

import java.util.Optional;

public interface LithostitchedFastNoiseConfigs {
	static FastNoiseConfig cellular(float frequency, int salt, CellularNoiseType.DistanceFunction distanceFunction, CellularNoiseType.ReturnType returnType, float jitter) {
		return new CellularNoiseType(frequency, salt, distanceFunction, returnType, jitter);
	}
	
	static FastNoiseConfig perlin(float frequency, int salt) {
		return new PerlinNoiseType(frequency, salt);
	}
	
	static FastNoiseConfig simplex(float frequency, int salt, FNL.FractalType fractalType, Optional<Integer> octaves, Optional<Float> lacunarity, Optional<Float> gain) {
		return new SimplexNoiseType(frequency, salt, fractalType, octaves, lacunarity, gain);
	}
}
