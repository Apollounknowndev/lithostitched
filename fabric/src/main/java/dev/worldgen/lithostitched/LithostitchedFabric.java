package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigHandler.load(FabricLoader.getInstance().getConfigDir().resolve("lithostitched.json"));
		LithostitchedBuiltInRegistries.init();
    }
}
