package dev.worldgen.lithostitched.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import dev.worldgen.lithostitched.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.worldgen.biomeinjector.region.RegionManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ParameterMap {
	public static final MapCodec<ParameterMap> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		Codec.unboundedMap(
			Codec.either(ClimateParameter.CODEC, DensityFunction.HOLDER_HELPER_CODEC),
			InclusiveRange.codec(Codec.DOUBLE)
		).fieldOf("parameters").forGetter(map -> map.parameters),
		Region.KEY_CODEC.optionalFieldOf("region").forGetter(map -> map.region)
	).apply(i, ParameterMap::new));
	
	private Map<Either<ClimateParameter, DensityFunction>, InclusiveRange<Double>> parameters;
	private Optional<ResourceKey<Region>> region;
	
	public ParameterMap(Map<Either<ClimateParameter, DensityFunction>, InclusiveRange<Double>> parameters, Optional<ResourceKey<Region>> region) {
		this.parameters = new HashMap<>(parameters);
		this.region = region;
	}
	
	public void mapAll(NoiseWiringHelper noiseHelper) {
		for (var entry : this.parameters.entrySet()) {
			var right = entry.getKey().right();
			right.ifPresent(densityFunction -> this.parameters.put(Either.right(densityFunction.mapAll(noiseHelper)), entry.getValue()));
		}
	}
	
	public boolean matches(DensityFunction.FunctionContext context, TargetPoint point, HashMap<DensityFunction, Double> densities, ResourceKey<Region> currentRegion) {
		for (var entry : this.parameters.entrySet()) {
			double density = entry.getKey().map(
				reserved -> reserved.getter.apply(point) / 10000D,
				df -> densities.computeIfAbsent(df, __ -> df.compute(context))
			);
			if (!entry.getValue().isValueInRange(density)) return false;
		}
		return region.map(currentRegion::equals).orElse(true);
	}
	
	public enum ClimateParameter implements StringRepresentable {
		CONTINENTALNESS("continentalness", TargetPoint::continentalness),
		EROSION("erosion", TargetPoint::erosion),
		WEIRDNESS("weirdness", TargetPoint::weirdness),
		HUMIDITY("humidity", TargetPoint::humidity),
		TEMPERATURE("temperature", TargetPoint::temperature),
		DEPTH("depth", TargetPoint::depth);
		
		public static final Codec<ClimateParameter> CODEC = StringRepresentable.fromEnum(ClimateParameter::values);
		
		public final String name;
		public final Function<TargetPoint, Long> getter;
		
		ClimateParameter(String name, Function<TargetPoint, Long> getter) {
			this.name = name;
			this.getter = getter;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
