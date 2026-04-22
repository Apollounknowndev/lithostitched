package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Event for adding region(s) just before regions are collected.
 * To add a region, add a listener in {@link AddRegionsEvent#EVENT}.
 * <p>
 * Example:
 * <pre>{@code
 * AddRegionsEvent.EVENT.register((registries, consumer) -> {
 *     consumer.accept(
 *         MyMod.id(...),
 *         Level.OVERWORLD,
 *         100
 *     );
 * });
 * }</pre>
 */
public interface AddRegionsEvent {
	LithostitchedEvent<AddRegionsEvent> EVENT = new LithostitchedEvent<>(callbacks -> (registries, consumer) -> {
		for (AddRegionsEvent callback : callbacks) {
			callback.addRegions(registries, consumer);
		}
	});
	
	void addRegions(RegistryAccess registries, RegionConsumer consumer);
	
	interface RegionConsumer {
		void accept(ResourceKey<Region> key, ResourceKey<Level> level, HolderSet<Biome> biomes, int weight);
	}
}
