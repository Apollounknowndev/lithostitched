package dev.worldgen.lithostitched.api.worldgen.biomeinjector;

import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterBuilderImpl;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterMap;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Builder of parameter conditions for replace_partially and force_placement biome injectors.
 */
@SuppressWarnings("unused")
public interface ParameterBuilder {
	static ParameterBuilder create() {
		return new ParameterBuilderImpl();
	}
	
	ParameterBuilder densityFunctionExactly(Holder<DensityFunction> densityFunction, double value);
	ParameterBuilder densityFunctionMin(Holder<DensityFunction> densityFunction, double min);
	ParameterBuilder densityFunctionMax(Holder<DensityFunction> densityFunction, double max);
	ParameterBuilder densityFunctionRange(Holder<DensityFunction> densityFunction, double min, double max);
	
	ParameterBuilder climateExactly(BiomeInjector.ClimateParameter climate, double value);
	ParameterBuilder climateMin(BiomeInjector.ClimateParameter climate, double min);
	ParameterBuilder climateMax(BiomeInjector.ClimateParameter climate, double max);
	ParameterBuilder climateRange(BiomeInjector.ClimateParameter climate, double min, double max);
	
	ParameterBuilder region(ResourceKey<Region> region);
	
	ParameterMap build();
}
