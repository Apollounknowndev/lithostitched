package dev.worldgen.lithostitched.api.worldgen.modifier.target;

import dev.worldgen.lithostitched.impl.worldgen.modifier.target.*;
import dev.worldgen.lithostitched.impl.worldgen.modifier.target.biome.ClimateTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.target.biome.HasFeatureTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.target.structure.FromBiomeTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface HolderTargets {
	
	// Operators
	
	@SafeVarargs
	static <T> HolderTarget<T> allOf(ResourceKey<Registry<T>> key, HolderTarget<T>... targets) {
		return new AllOfTarget<>(key, List.of(targets));
	}
	
	@SafeVarargs
	static <T> HolderTarget<T> anyOf(ResourceKey<Registry<T>> key, HolderTarget<T>... targets) {
		return new AnyOfTarget<>(key, List.of(targets));
	}
	
	static <T> HolderTarget<T> not(ResourceKey<Registry<T>> key, HolderTarget<T> target) {
		return new NotTarget<>(key, target);
	}
	
	// Basic
	
	static <T> HolderTarget<T> holderSet(Holder<T> holder) {
		var key = holder.unwrapKey().orElseThrow().registryKey();
		return new HolderSetTarget<>(key, HolderSet.direct(holder));
	}
	
	static <T> HolderTarget<T> holderSet(HolderSet<T> holderSet) {
		return holderSet(holderSet.get(0).unwrapKey().orElseThrow().registryKey(), holderSet);
	}
	
	static <T> HolderTarget<T> holderSet(ResourceKey<Registry<T>> key, HolderSet<T> holderSet) {
		return new HolderSetTarget<>(key, holderSet);
	}
	
	static <T> HolderTarget<T> any(ResourceKey<Registry<T>> key) {
		return new AnyTarget<>(key);
	}
	
	static <T> HolderTarget<T> regex(ResourceKey<Registry<T>> key, String pattern) {
		return new RegexTarget<>(key, pattern);
	}
	
	// Special
	
	static HolderTarget<Biome> climate(Optional<InclusiveRange<Float>> temperature, Optional<InclusiveRange<Float>> downfall, Optional<Boolean> hasPrecipitation, Optional<Biome.TemperatureModifier> temperatureModifier) {
		return new ClimateTarget<>(Registries.BIOME, temperature, downfall, hasPrecipitation, temperatureModifier);
	}
	
	static HolderTarget<Biome> hasFeature(Holder<PlacedFeature> feature) {
		return new HasFeatureTarget<>(Registries.BIOME, feature);
	}
	
	static HolderTarget<Structure> fromBiome(Holder<Biome> biome) {
		return new FromBiomeTarget<>(Registries.STRUCTURE, biome);
	}
}
