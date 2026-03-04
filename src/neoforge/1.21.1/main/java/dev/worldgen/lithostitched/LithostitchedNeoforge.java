package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(Lithostitched.MOD_ID)
public final class LithostitchedNeoforge {
	public LithostitchedNeoforge(IEventBus bus) {
		ConfigHandler.load();
		LithostitchedBuiltInRegistries.init();
		LithostitchedRegistrations.init(bus);
	}
}
