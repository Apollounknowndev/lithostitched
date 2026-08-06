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
	private Map<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> parameters = new HashMap<>();
	private Optional<ResourceKey<Region>> region = Optional.empty();
	
	@Override
	public ParameterBuilder densityFunctionExactly(Holder<DensityFunction> densityFunction, double value) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(value));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionMin(Holder<DensityFunction> densityFunction, double min) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(min, Double.MAX_VALUE));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionMax(Holder<DensityFunction> densityFunction, double max) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(-Double.MAX_VALUE, max));
		return this;
	}
	
	@Override
	public ParameterBuilder densityFunctionRange(Holder<DensityFunction> densityFunction, double min, double max) {
		this.parameters.put(Either.right(new DensityFunctions.HolderHolder(densityFunction)), new InclusiveRange<>(min, max));
		return this;
	}
	
	@Override
	public ParameterBuilder climateExactly(BiomeInjector.ClimateParameter climate, double value) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(value));
		return this;
	}
	
	@Override
	public ParameterBuilder climateMin(BiomeInjector.ClimateParameter climate, double min) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(min, Double.MAX_VALUE));
		return this;
	}
	
	@Override
	public ParameterBuilder climateMax(BiomeInjector.ClimateParameter climate, double max) {
		this.parameters.put(Either.left(climate), new InclusiveRange<>(-Double.MAX_VALUE, max));
		return this;
	}
	
	@Override
	public ParameterBuilder climateRange(BiomeInjector.ClimateParameter climate, double min, double max) {
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
