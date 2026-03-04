package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.Lithostitched;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.Map;

import static net.minecraft.core.RegistryAccess.fromRegistryOfRegistries;

public class LithostitchedRegistrarActual {
	@Actual
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		DynamicRegistries.register(key, codec);
	}
	
	@Actual
	public static <T> void register(ResourceKey<Registry<T>> key, Map<String, T> entries) {
		Registry<T> registry = fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).registryOrThrow(key);
		for (var entry : entries.entrySet()) {
			Registry.register(registry, Lithostitched.id(entry.getKey()), entry.getValue());
		}
	}
}