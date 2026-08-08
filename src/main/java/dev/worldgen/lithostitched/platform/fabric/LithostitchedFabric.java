//? if fabric {
package dev.worldgen.lithostitched.platform.fabric;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.platform.fabric.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

public final class LithostitchedFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Lithostitched.init();
		ResourceConditions.register(BreaksSeedParityCondition.TYPE);
		
		PayloadTypeRegistry.clientboundPlay().register(
			ApplyStructureAttributesPacket.TYPE,
			ApplyStructureAttributesPacket.CODEC
		);
		
		ServerTickEvents.START_LEVEL_TICK.register(StructureAttributeHandler::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, _) -> StructureAttributeHandler.disconnect(handler.getPlayer()));
	}
}
//? }