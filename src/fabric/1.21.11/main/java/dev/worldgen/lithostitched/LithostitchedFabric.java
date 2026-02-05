package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigHandler.load(FabricLoader.getInstance().getConfigDir().resolve("lithostitched.json"));
		LithostitchedBuiltInRegistries.init();
		LithostitchedRegistrations.init();
		
		PayloadTypeRegistry.playS2C().register(
			ApplyStructureAttributesPacket.TYPE,
			ApplyStructureAttributesPacket.CODEC
		);
		
		ServerTickEvents.START_WORLD_TICK.register(StructureAttributeHandler::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> StructureAttributeHandler.disconnect(handler.getPlayer()));
	}
}
