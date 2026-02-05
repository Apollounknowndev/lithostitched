package dev.worldgen.lithostitched.client;

import com.mojang.serialization.DynamicOps;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.duck.StructureAttributesHolder;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(value = Lithostitched.MOD_ID, dist = Dist.CLIENT)
public final class LithostitchedNeoforgeClient {
	public LithostitchedNeoforgeClient(IEventBus bus) {
		bus.addListener(this::registerPacketHandlers);
		NeoForge.EVENT_BUS.addListener(this::onLevelTick);
	}
	
	private void registerPacketHandlers(RegisterClientPayloadHandlersEvent event) {
		event.register(
			ApplyStructureAttributesPacket.TYPE,
			(payload, context) -> {
				Level level = context.player().level();
				DynamicOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
				StructureAttributesHolder.from(level).updateStructureAttributes(payload.getAttributes(ops));
			}
		);
	}
	
	private void onLevelTick(LevelTickEvent.Pre event) {
		if (event.getLevel() instanceof ClientLevel level) {
			StructureAttributesHolder.from(level).incrementTicksSinceUpdated();
		}
	}
}
