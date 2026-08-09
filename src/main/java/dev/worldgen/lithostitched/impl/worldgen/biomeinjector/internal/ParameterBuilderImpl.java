package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.ParameterBuilder;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParameterBuilderImpl implements ParameterBuilder {
	private Map<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Float>> parameters = new HashMap<>();
	private Optional<ResourceKey<Region>> region = Optional.empty();
	
	@Override
	public ParameterBuilder densityFunctionExactly(Holder<DensityFunction> densityFunction, float value) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(value));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionMin(Holder<DensityFunction> densityFunction, float min) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(min, Float.MAX_VALUE));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionMax(Holder<DensityFunction> densityFunction, float max) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(-Float.MAX_VALUE, max));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionRange(Holder<DensityFunction> densityFunction, float min, float max) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(min, max));
		return this;
	}
	
	@Override
	public ParameterBuilder climateExactly(BiomeInjector.ClimateParameter climate, float value) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(value));
		return this;
	}
	
	@Override
	public ParameterBuilder climateMin(BiomeInjector.ClimateParameter climate, float min) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(min, Float.MAX_VALUE));
		return this;
	}
	
	@Override
	public ParameterBuilder climateMax(BiomeInjector.ClimateParameter climate, float max) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(-Float.MAX_VALUE, max));
		return this;
	}
	
	@Override
	public ParameterBuilder climateRange(BiomeInjector.ClimateParameter climate, float min, float max) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(min, max));
		return this;
	}
	
	@Override
	public ParameterBuilder region(ResourceKey<Region> region) {
		this.region = Optional.of(region);
		return this;
	}
	
	@Override
	public ParameterMap build() {
		return new ParameterMap(parameters, region);
	}
}
