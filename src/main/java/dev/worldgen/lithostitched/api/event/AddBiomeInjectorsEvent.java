package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

/**
 * Event for adding biome injector(s) just before they are sorted and applied.
 * To add a modifier, add a listener in {@link AddBiomeInjectorsEvent#EVENT}.
 * <p>
 * Example:
 * <pre>{@code
 * AddBiomeInjectorsEvent.EVENT.register((registries, consumer) -> {
 *     consumer.accept(
 *         MyMod.id(...),
 *         BiomeInjector.builder(Level.OVERWORLD).replaceFully(...)
 *     );
 * });
 * }</pre>
 */
public interface AddBiomeInjectorsEvent {
	LithostitchedEvent<AddBiomeInjectorsEvent> EVENT = new LithostitchedEvent<>(callbacks -> ((registries, consumer) -> {
		for (AddBiomeInjectorsEvent callback : callbacks) {
			callback.addInjectors(registries, consumer);
		}
	}));
	
	void addInjectors(RegistryAccess registries, BiConsumer<ResourceLocation, BiomeInjector> consumer);
}
