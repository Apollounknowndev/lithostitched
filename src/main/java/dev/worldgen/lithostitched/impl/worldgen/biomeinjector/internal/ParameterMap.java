package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector.ClimateParameter;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ParameterMap {
	public static final MapCodec<ParameterMap> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		Codec.unboundedMap(
			Codec.either(ClimateParameter.CODEC, DensityFunction.CODEC),
			LithostitchedCodecs.FLOAT_RANGE
		).fieldOf("parameters").forGetter(map -> map.parameters),
		Region.KEY_CODEC.optionalFieldOf("region").forGetter(map -> map.region)
	).apply(i, ParameterMap::new));
	
	private Map<Either<ClimateParameter, DensityFunction>, InclusiveRange<Float>> parameters;
	private Optional<ResourceKey<Region>> region;
	
	public ParameterMap(Map<Either<ClimateParameter, DensityFunction>, InclusiveRange<Float>> parameters, Optional<ResourceKey<Region>> region) {
		this.parameters = new HashMap<>(parameters);
		this.region = region;
	}
	
	public void mapAll(DensityFunctionWrapper noiseHelper) {
		Map<Either<ClimateParameter, DensityFunction>, InclusiveRange<Float>> mappedParameters = new HashMap<>();
		for (var entry : this.parameters.entrySet()) {
			var either = entry.getKey();
			mappedParameters.put(either.mapRight(df -> df.mapAll(noiseHelper)), entry.getValue());
		}
		this.parameters.clear();
		this.parameters.putAll(mappedParameters);
	}
	
	public boolean matches(DensityFunction.FunctionContext context, TargetPoint point, HashMap<DensityFunction, Float> densities, ResourceKey<Region> currentRegion) {
		if (!region.map(currentRegion::equals).orElse(true)) return false;
		for (var entry : this.parameters.entrySet()) {
			float density = entry.getKey().map(
				reserved -> reserved.getter.apply(point) / 10000F,
				df -> densities.computeIfAbsent(df, __ -> df.compute(context))
			);
			if (!entry.getValue().isValueInRange(density)) return false;
		}
		return true;
	}
}
