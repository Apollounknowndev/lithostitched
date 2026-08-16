package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.Lithostitched;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

//? if fabric {
/*import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
 *///? } else {
import dev.worldgen.lithostitched.platform.neoforge.LithostitchedNeoforge;
import dev.worldgen.lithostitched.platform.neoforge.LithostitchedRegistrations;
//? }

public class LithostitchedRegistrar {
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		//? if fabric {
		/*DynamicRegistries.register(key, codec);
		 *///? } else {
		LithostitchedRegistrations.DYNAMIC_REGISTRIES.add(LithostitchedNeoforge.registerDynamicRegistry(key, codec));
		//? }
	}
	
	public static <T> void register(Registry<T> key, Map<String, T> entries) {
		for (var entry : entries.entrySet()) {
			Lithostitched.REGISTRAR.register(key, entry.getKey(), entry.getValue());
		}
	}
}
