package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(LithostitchedCommon.MOD_ID)
public final class LithostitchedNeoforge {

	public LithostitchedNeoforge(IEventBus bus) {
		LithostitchedCommon.init();
		LithostitchedBuiltInRegistries.init(bus);
	}

}
