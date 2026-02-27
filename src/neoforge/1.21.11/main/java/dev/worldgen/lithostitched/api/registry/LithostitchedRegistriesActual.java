package dev.worldgen.lithostitched.api.registry;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedRegistriesActual {
	@Actual
	public static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
		return (Registry<T>) LithostitchedBuiltInRegistries.REGISTRIES.get(key);
	}
}