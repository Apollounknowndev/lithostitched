package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class LithostitchedRegistrarActual {
	@Actual
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		LithostitchedRegistrations.DYNAMIC_REGISTRIES.add(event -> event.dataPackRegistry(key, codec));
	}
	
	@Actual
	static <T> void register(Registry<T> registry, Map<String, T> entries) {
		if (LithostitchedRegistrations.REGISTER_CACHE.containsKey(registry.key())) {
			Lithostitched.LOGGER.warn("Attempted to register Lithostitched objects twice, this shouldn't happen!");
			return;
		}
		DeferredRegister<T> register = DeferredRegister.create(registry.key(), Lithostitched.MOD_ID);
		LithostitchedRegistrations.REGISTER_CACHE.put(registry.key(), register);
		for (var entry : entries.entrySet()) {
			register.register(entry.getKey(), entry::getValue);
		}
	}
}
