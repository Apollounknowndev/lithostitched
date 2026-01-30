package dev.worldgen.lithostitched.worldgen.structure;

import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.multiplatform.annotations.Actual;

public class StructureAttributeHandlerActual {
	@Actual
	private static void sendPacket(ServerPlayer player, ApplyStructureAttributesPacket packet) {
		ServerPlayNetworking.send(player, packet);
	}
}
