package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;
import java.util.function.Function;

public interface BiomeInjector {
	@SuppressWarnings("unchecked")
	Codec<BiomeInjector> CODEC = Codec.lazyInitialized(() -> {
		var registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.BIOME_INJECTOR_TYPE.identifier());
		if (registry.isEmpty()) throw new NullPointerException("Bandlands band type registry does not exist yet!");
		return ((Registry<MapCodec<? extends BiomeInjector>>) registry.get()).byNameCodec();
	}).dispatch(BiomeInjector::codec, Function.identity());
	MapCodec<ResourceKey<LevelStem>> DIMENSION_CODEC = ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension");
	MapCodec<Integer> PRIORITY_CODEC = Codec.INT.optionalFieldOf("priority", 1000);
	
	ResourceKey<LevelStem> dimension();
	
	int priority();
	
	List<Holder<Biome>> possibleBiomes();
	
	default void mapAll(NoiseWiringHelper noiseHelper) {
	
	}
	
	MapCodec<? extends BiomeInjector> codec();
}
