package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(LithostitchedCommon.MOD_ID)
public final class LithostitchedNeoforge {
	public LithostitchedNeoforge(IEventBus bus) {
		ConfigHandler.load(FMLPaths.CONFIGDIR.get().resolve("lithostitched.json"));
		LithostitchedBuiltInRegistries.init(bus);
	}
}
