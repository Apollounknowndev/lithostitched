package dev.worldgen.lithostitched.client;

import com.mojang.serialization.DynamicOps;
import dev.worldgen.lithostitched.duck.StructureAttributesHolder;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class LithostitchedFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
			ApplyStructureAttributesPacket.TYPE,
			(payload, context) -> {
				ClientLevel level = context.client().level;
				DynamicOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
				StructureAttributesHolder.from(level).updateStructureAttributes(payload.getAttributes(ops));
			}
		);
		ClientTickEvents.START_WORLD_TICK.register(level -> {
			StructureAttributesHolder.from(level).incrementTicksSinceUpdated();
		});
	}
}
