package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.DynamicOps;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.HashMap;
import java.util.Map;

public class StructureAttributeHandler {
	public static final int DELAY_BETWEEN_STRUCTURE_CHECKS = 5;
	public static final int STRUCTURE_ATTRIBUTE_LERP = 40;
	
	private static final Map<ServerPlayer, Structure> playersToStructures = new HashMap<>();
	private static int delay = DELAY_BETWEEN_STRUCTURE_CHECKS;
	
	public static void tick(ServerLevel level) {
		delay--;
		if (delay > 0) return;
		
		delay = DELAY_BETWEEN_STRUCTURE_CHECKS;
		DynamicOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		StructureManager manager = level.structureManager();
		for (ServerPlayer player : level.players()) {
			BlockPos pos = player.blockPosition();
			if (!level.isLoaded(pos)) continue;
			
			Structure structure = manager.getStructureWithPieceAt(pos, holder -> true).getStructure();
   			if (hasStructureNotChanged(player, structure)) continue;
			
			if (structure == null) {
				updateAttributes(player, ops, null);
				continue;
			}
			if (structure instanceof DelegatingStructure delegating) {
				EnvironmentAttributeMap attributes = (EnvironmentAttributeMap) delegating.config().getAttributes();
				if (!attributes.keySet().isEmpty()) {
					updateAttributes(player, ops, structure);
				}
			}
		}
	}
	
	public static void disconnect(ServerPlayer player) {
		playersToStructures.remove(player);
	}
	
	private static boolean hasStructureNotChanged(ServerPlayer player, Structure structure) {
		return structure == playersToStructures.getOrDefault(player, null);
	}
	
	private static void updateAttributes(ServerPlayer player, DynamicOps<Tag> ops, Structure structure) {
		playersToStructures.put(player, structure);
		if (structure == null) {
			sendPacket(player, ApplyStructureAttributesPacket.createEmpty());
		}
		if (structure instanceof DelegatingStructure delegating) {
			sendPacket(player, ApplyStructureAttributesPacket.create(ops, (EnvironmentAttributeMap) delegating.config().getAttributes()));
		}
	}
	
	@Expect
	private static void sendPacket(ServerPlayer player, ApplyStructureAttributesPacket packet);
}
