package dev.worldgen.lithostitched.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ParameterMap {
	public static final MapCodec<ParameterMap> CODEC = Codec.unboundedMap(
		Codec.either(ReservedParameter.CODEC, DensityFunction.HOLDER_HELPER_CODEC),
		InclusiveRange.codec(Codec.DOUBLE)
	).fieldOf("parameters").xmap(ParameterMap::new, p -> p.parameters);
	private Map<Either<ReservedParameter, DensityFunction>, InclusiveRange<Double>> parameters;
	
	public ParameterMap(Map<Either<ReservedParameter, DensityFunction>, InclusiveRange<Double>> parameters) {
		this.parameters = new HashMap<>(parameters);
	}
	
	public void mapAll(NoiseWiringHelper noiseHelper) {
		for (var entry : this.parameters.entrySet()) {
			var right = entry.getKey().right();
			right.ifPresent(densityFunction -> this.parameters.put(Either.right(densityFunction.mapAll(noiseHelper)), entry.getValue()));
		}
	}
	
	public boolean matches(DensityFunction.FunctionContext context, TargetPoint point, HashMap<DensityFunction, Double> densities) {
		for (var entry : this.parameters.entrySet()) {
			double density = entry.getKey().map(
				reserved -> reserved.getter.apply(point) / 10000D,
				df -> densities.computeIfAbsent(df, __ -> df.compute(context))
			);
			if (!entry.getValue().isValueInRange(density)) return false;
		}
		return true;
	}
	
	public enum ReservedParameter implements StringRepresentable {
		CONTINENTALNESS("continentalness", TargetPoint::continentalness),
		EROSION("erosion", TargetPoint::erosion),
		WEIRDNESS("weirdness", TargetPoint::weirdness),
		HUMIDITY("humidity", TargetPoint::humidity),
		TEMPERATURE("temperature", TargetPoint::temperature),
		DEPTH("depth", TargetPoint::depth);
		
		public static final Codec<ReservedParameter> CODEC = StringRepresentable.fromEnum(ReservedParameter::values);
		
		public final String name;
		public final Function<TargetPoint, Long> getter;
		
		ReservedParameter(String name, Function<TargetPoint, Long> getter) {
			this.name = name;
			this.getter = getter;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
}
