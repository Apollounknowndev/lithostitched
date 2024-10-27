package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.registry.LithostitchedTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		LithostitchedBuiltInRegistries.init();

		if (ConfigHandler.getConfig().breaksSeedParity()) {
			ResourceManagerHelper.registerBuiltinResourcePack(
				LithostitchedCommon.id("breaks_seed_parity"),
				FabricLoader.getInstance().getModContainer("lithostitched").get(),
				Component.literal("Lithostitched extras"),
				ResourcePackActivationType.ALWAYS_ENABLED
			);
		}
	}
}
