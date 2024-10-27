package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.RegistryDataLoader;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		LithostitchedBuiltInRegistries.init();
	}
}
