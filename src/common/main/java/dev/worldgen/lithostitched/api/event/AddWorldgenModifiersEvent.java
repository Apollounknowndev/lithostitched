package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

/**
 * Event for adding worldgen modifier(s) just before they are sorted and ran.
 * To add a modifier, add a listener in {@link AddWorldgenModifiersEvent#EVENT}.
 * <p>
 * Example:
 * <pre>{@code
 * AddWorldgenModifiersEvent.EVENT.register((registries, consumer) -> {
 *     consumer.accept(
 *         MyMod.id(...),
 *         WorldgenModifier.builder().addFeatures(...)
 *     );
 * });
 * }</pre>
 */
public interface AddWorldgenModifiersEvent {
	LithostitchedEvent<AddWorldgenModifiersEvent> EVENT = new LithostitchedEvent<>(callbacks -> ((registries, consumer) -> {
		for (AddWorldgenModifiersEvent callback : callbacks) {
			callback.addModifiers(registries, consumer);
		}
	}));
	
	void addModifiers(RegistryAccess registries, BiConsumer<Identifier, WorldgenModifier> consumer);
}
