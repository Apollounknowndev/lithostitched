package dev.worldgen.lithostitched.worldgen.structure;

import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.neoforge.network.PacketDistributor;

public class StructureAttributeHandlerActual {
	@Actual
	private static void sendPacket(ServerPlayer player, ApplyStructureAttributesPacket packet) {
		PacketDistributor.sendToPlayer(player, packet);
	}
}
