package dev.worldgen.lithostitched.api.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedRegistriesActual {
	@Actual
	public static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}
}
