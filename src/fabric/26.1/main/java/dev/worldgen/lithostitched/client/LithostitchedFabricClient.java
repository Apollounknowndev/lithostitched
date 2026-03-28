package dev.worldgen.lithostitched.client;

import com.mojang.serialization.DynamicOps;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.duck.StructureAttributesHolder;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

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
		ClientTickEvents.START_LEVEL_TICK.register(level -> {
			StructureAttributesHolder.from(level).incrementTicksSinceUpdated();
		});
		
		DebugScreenEntries.register(Lithostitched.id("region"), (displayer, serverOrClientLevel, clientChunk, serverChunk) -> {
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.getCameraEntity();
			if (minecraft.level == null || entity == null) return;
			BlockPos pos = entity.blockPosition();
			
			if (serverOrClientLevel instanceof ServerLevel serverLevel) {
				if (serverLevel.getChunkSource().getGenerator().getBiomeSource() instanceof InjectorBiomeSource injector) {
					displayer.addLine(injector.getRegionLine(pos));
				}
			}
		});
	}
}
