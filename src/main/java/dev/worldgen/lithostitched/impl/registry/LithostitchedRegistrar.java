package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.Lithostitched;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class LithostitchedRegistrar {
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		Lithostitched.REGISTRAR.registerRegistry(key, codec);
	}
	
	public static <T> void register(Registry<T> key, Map<String, T> entries) {
		for (var entry : entries.entrySet()) {
			Lithostitched.REGISTRAR.register(key, entry.getKey(), entry.getValue());
		}
	}
}
