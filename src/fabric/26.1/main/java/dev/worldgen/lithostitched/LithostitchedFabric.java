package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigHandler.load();
		LithostitchedBuiltInRegistries.init();
		ResourceConditions.register(BreaksSeedParityCondition.TYPE);
		
		PayloadTypeRegistry.clientboundPlay().register(
			ApplyStructureAttributesPacket.TYPE,
			ApplyStructureAttributesPacket.CODEC
		);
		
		ServerTickEvents.START_LEVEL_TICK.register(StructureAttributeHandler::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, _) -> StructureAttributeHandler.disconnect(handler.getPlayer()));
	}
}
