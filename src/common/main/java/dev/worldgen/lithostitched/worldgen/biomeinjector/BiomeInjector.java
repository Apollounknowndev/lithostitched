package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class BiomeInjector {
	public static final Codec<BiomeInjector> CODEC = RecordCodecBuilder.<BiomeInjector>create(i -> i.group(
		Biome.CODEC.fieldOf("biome").forGetter(BiomeInjector::biome),
		ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension").forGetter(BiomeInjector::dimension),
		Codec.unboundedMap(
			Codec.either(Codec.STRING, DensityFunction.HOLDER_HELPER_CODEC),
			InclusiveRange.codec(Codec.DOUBLE)
		).fieldOf("parameters").forGetter(BiomeInjector::parameters)
	).apply(i, BiomeInjector::new)).validate(BiomeInjector::validate);
	private static final Map<String, Function<TargetPoint, Long>> RESERVED_PARAMETERS = Map.of(
		"continentalness", TargetPoint::continentalness,
		"erosion", TargetPoint::erosion,
		"weirdness", TargetPoint::weirdness,
		"humidity", TargetPoint::humidity,
		"temperature", TargetPoint::temperature,
		"depth", TargetPoint::depth
	);
	
	private final Holder<Biome> biome;
	private final ResourceKey<LevelStem> dimension;
	private final Map<Either<String, DensityFunction>, InclusiveRange<Double>> parameters;
	
	public BiomeInjector(Holder<Biome> biome, ResourceKey<LevelStem> dimension, Map<Either<String, DensityFunction>, InclusiveRange<Double>> parameters) {
		this.biome = biome;
		this.dimension = dimension;
		this.parameters = new HashMap<>(parameters);
	}
	
	private DataResult<BiomeInjector> validate() {
		for (var entry : this.parameters.entrySet()) {
			var either = entry.getKey();
			if (either.left().isPresent() && !RESERVED_PARAMETERS.containsKey(either.left().get())) {
				return DataResult.error(() -> "Value found for non-existent parameter: " + either.left().get());
			}
		}
		return DataResult.success(this);
	}
	
	public void mapAll(NoiseWiringHelper noiseHelper) {
		for (var entry : this.parameters.entrySet()) {
			var right = entry.getKey().right();
			right.ifPresent(densityFunction -> this.parameters.put(Either.right(densityFunction.mapAll(noiseHelper)), entry.getValue()));
		}
	}
	
	public boolean matches(DensityFunction.FunctionContext context, TargetPoint point) {
		for (var entry : this.parameters.entrySet()) {
			double density = entry.getKey().map(
				string -> RESERVED_PARAMETERS.get(string).apply(point) / 10000D,
				df -> df.compute(context)
			);
			if (!entry.getValue().isValueInRange(density)) return false;
		}
		return true;
	}
	
	public Holder<Biome> biome() {
		return biome;
	}
	
	public ResourceKey<LevelStem> dimension() {
		return dimension;
	}
	
	private Map<Either<String, DensityFunction>, InclusiveRange<Double>> parameters() {
		return parameters;
	}
}
