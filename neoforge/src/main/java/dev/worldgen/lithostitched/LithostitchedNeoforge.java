package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(LithostitchedCommon.MOD_ID)
public final class LithostitchedNeoforge {

	public LithostitchedNeoforge() {
		LithostitchedCommon.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		LithostitchedBuiltInRegistries.init(bus);
	}

}
