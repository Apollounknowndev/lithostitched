package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class BiomeInjector {
	public static final Codec<BiomeInjector> CODEC = RecordCodecBuilder.<BiomeInjector>create(i -> i.group(
		Biome.CODEC.fieldOf("biome").forGetter(BiomeInjector::biome),
		ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension").forGetter(BiomeInjector::dimension),
		Codec.unboundedMap(Codec.STRING, DensityFunction.HOLDER_HELPER_CODEC).fieldOf("parameters").forGetter(BiomeInjector::parameters),
		Codec.unboundedMap(Codec.STRING, InclusiveRange.codec(Codec.DOUBLE)).fieldOf("values").forGetter(BiomeInjector::values)
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
	private final HashMap<String, DensityFunction> parameters;
	private final Map<String, InclusiveRange<Double>> values;
	
	public BiomeInjector(Holder<Biome> biome, ResourceKey<LevelStem> dimension, Map<String, DensityFunction> parameters, Map<String, InclusiveRange<Double>> values) {
		this.biome = biome;
		this.dimension = dimension;
		this.parameters = new HashMap<>(parameters);
		this.values = values;
	}
	
	private DataResult<BiomeInjector> validate() {
		for (var entry : this.values.entrySet()) {
			String key = entry.getKey();
			if (RESERVED_PARAMETERS.containsKey(key) && this.parameters.containsKey(key)) {
				return DataResult.error(() -> "Density function provided for reserved parameter name: " + key);
			} else if (!this.parameters.containsKey(key)) {
				return DataResult.error(() -> "Value found for non-existent parameter: " + key);
			}
		}
		return DataResult.success(this);
	}
	
	public void mapAll(NoiseWiringHelper noiseHelper) {
		this.parameters.replaceAll((k, v) -> v.mapAll(noiseHelper));
	}
	
	public boolean matches(DensityFunction.FunctionContext context, TargetPoint point) {
		for (var entry : this.values.entrySet()) {
			String key = entry.getKey();
			double density;
			if (RESERVED_PARAMETERS.containsKey(key)) {
				density = RESERVED_PARAMETERS.get(key).apply(point) / 10000D;
			} else {
				density = this.parameters.get(entry.getKey()).compute(context);
			}
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
	
	public Map<String, DensityFunction> parameters() {
		return parameters;
	}
	
	public Map<String, InclusiveRange<Double>> values() {
		return values;
	}
}
