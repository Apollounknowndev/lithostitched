package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.ClocheHacks;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.LithostitchedNeoforge;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class LithostitchedRegistrarActual {
	@Actual
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		LithostitchedRegistrations.DYNAMIC_REGISTRIES.add(LithostitchedNeoforge.registerDynamicRegistry(key, codec));
	}
	
	@Actual
	public static <T> void register(ResourceKey<Registry<T>> key, Map<String, T> entries) {
		DeferredRegister<T> register = (DeferredRegister<T>) LithostitchedRegistrations.REGISTER_CACHE.computeIfAbsent(key, __ -> DeferredRegister.create(key, Lithostitched.MOD_ID));
		for (var entry : entries.entrySet()) {
			register.register(entry.getKey(), entry::getValue);
		}
	}
}
