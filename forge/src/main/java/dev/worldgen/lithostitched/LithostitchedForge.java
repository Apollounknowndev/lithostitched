package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(LithostitchedCommon.MOD_ID)
public final class LithostitchedForge {

	public LithostitchedForge() {
		LithostitchedCommon.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		LithostitchedBuiltInRegistries.init(bus);
	}

}
