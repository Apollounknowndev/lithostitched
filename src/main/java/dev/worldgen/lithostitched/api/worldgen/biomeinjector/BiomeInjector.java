package dev.worldgen.lithostitched.api.worldgen.biomeinjector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.event.AddBiomeInjectorsEvent;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The base interface for a biome injector.
 * <p>
 * Biome injectors are used to add/replace biomes in the biome layout.
 * <p>
 * This interface is public only for usage in datagen and {@link AddBiomeInjectorsEvent}. This is <i>not</i> meant to be an interface extended upon by other mods.
 */
public interface BiomeInjector {
	@SuppressWarnings("unchecked")
	Codec<BiomeInjector> CODEC = LithostitchedBuiltInRegistries.BIOME_INJECTOR_TYPE.byNameCodec().dispatch(BiomeInjector::codec, Function.identity());
	Integer DEFAULT_PRIORITY = 1000;
	
	MapCodec<ResourceKey<LevelStem>> DIMENSION_CODEC = ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension");
	MapCodec<Integer> PRIORITY_CODEC = Codec.INT.optionalFieldOf("priority", DEFAULT_PRIORITY);
	
	Optional<LoadPredicate> predicate();
	ResourceKey<LevelStem> dimension();
	int priority();
	
	List<Holder<Biome>> possibleBiomes();
	
	default void mapAll(DensityFunctionWrapper noiseHelper) {
	
	}
	
	MapCodec<? extends BiomeInjector> codec();
	
	enum ClimateParameter implements StringRepresentable {
		CONTINENTALNESS("continentalness", Climate.TargetPoint::continentalness),
		EROSION("erosion", Climate.TargetPoint::erosion),
		WEIRDNESS("weirdness", Climate.TargetPoint::weirdness),
		HUMIDITY("humidity", Climate.TargetPoint::humidity),
		TEMPERATURE("temperature", Climate.TargetPoint::temperature),
		DEPTH("depth", Climate.TargetPoint::depth);
		
		public static final Codec<ClimateParameter> CODEC = StringRepresentable.fromEnum(ClimateParameter::values);
		
		public final String name;
		public final Function<Climate.TargetPoint, Long> getter;
		
		ClimateParameter(String name, Function<Climate.TargetPoint, Long> getter) {
			this.name = name;
			this.getter = getter;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
	// Builder nonsense
	
	static InjectorBuilder builder(ResourceKey<Level> level) {
		return new InjectorBuilder(level, Optional.empty());
	}
	
	static InjectorBuilder builder(ResourceKey<Level> level, LoadPredicate predicate) {
		return new InjectorBuilder(level, Optional.of(predicate));
	}
	
	@SuppressWarnings("unused")
	class InjectorBuilder {
		private final ResourceKey<LevelStem> level;
		private final Optional<LoadPredicate> predicate;
		private Optional<Integer> priority = Optional.empty();
		
		private InjectorBuilder(ResourceKey<Level> level, Optional<LoadPredicate> predicate) {
			this.level = Registries.levelToLevelStem(level);
			this.predicate = predicate;
		}
		
		public InjectorBuilder priority(int priority) {
			this.priority = Optional.of(priority);
			return this;
		}
		
		public BiomeInjector addPoints(Climate.ParameterList<Holder<Biome>> points) {
			return new AddPoints(predicate, level, priority.orElse(DEFAULT_PRIORITY), points);
		}
		
		public BiomeInjector dispatchAlternateLayout(ParameterBuilder parameterBuilder, Climate.ParameterList<Holder<Biome>> points) {
			return new DispatchAlternateLayout(predicate, level, priority.orElse(DEFAULT_PRIORITY), parameterBuilder.build(), points);
		}
		
		public BiomeInjector forcePlacement(Holder<Biome> biome, ParameterBuilder parameterBuilder) {
			return new ForcePlacement(predicate, level, priority.orElse(DEFAULT_PRIORITY), biome, parameterBuilder.build());
		}
		
		public BiomeInjector replaceFully(Holder<Biome> target, Holder<Biome> replacement) {
			return new ReplaceFully(predicate, level, priority.orElse(DEFAULT_PRIORITY), HolderSet.direct(target), replacement);
		}
		
		public BiomeInjector replaceFully(HolderSet<Biome> targets, Holder<Biome> replacement) {
			return new ReplaceFully(predicate, level, priority.orElse(DEFAULT_PRIORITY), targets, replacement);
		}
		
		public BiomeInjector replacePartially(Holder<Biome> target, Holder<Biome> replacement, ParameterBuilder parameterBuilder) {
			return new ReplacePartially(predicate, level, priority.orElse(DEFAULT_PRIORITY), HolderSet.direct(target), replacement, parameterBuilder.build());
		}
		
		public BiomeInjector replacePartially(HolderSet<Biome> targets, Holder<Biome> replacement, ParameterBuilder parameterBuilder) {
			return new ReplacePartially(predicate, level, priority.orElse(DEFAULT_PRIORITY), targets, replacement, parameterBuilder.build());
		}
	}
}
