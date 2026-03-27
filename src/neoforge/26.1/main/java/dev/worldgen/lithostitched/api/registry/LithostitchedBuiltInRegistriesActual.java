package dev.worldgen.lithostitched.api.registry;

import dev.worldgen.lithostitched.LithostitchedNeoforge;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class LithostitchedBuiltInRegistriesActual {
	@Actual
	private static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
		DeferredRegister<T> register = LithostitchedRegistrations.createDeferredRegister(key);
		return register.makeRegistry(LithostitchedNeoforge.emptyConsumer());
	}
}