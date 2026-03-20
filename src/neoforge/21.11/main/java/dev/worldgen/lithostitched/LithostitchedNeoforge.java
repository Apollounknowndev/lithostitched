package dev.worldgen.lithostitched;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Consumer;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(Lithostitched.MOD_ID)
public final class LithostitchedNeoforge {
	public LithostitchedNeoforge(IEventBus bus) {
		ConfigHandler.load();
		LithostitchedBuiltInRegistries.init();
		LithostitchedRegistrations.init(bus);
		
		bus.addListener(this::registerPayloadHandlers);
		NeoForge.EVENT_BUS.addListener(this::onStartWorldTick);
		NeoForge.EVENT_BUS.addListener(this::onPlayerDisconnect);
	}
	
	public static <T> Consumer<DataPackRegistryEvent.NewRegistry> registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		return event -> event.dataPackRegistry(key, codec);
	}
	
	public static <T> Consumer<RegistryBuilder<T>> emptyConsumer() {
		return b -> {};
	}
	
	private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(ApplyStructureAttributesPacket.TYPE, ApplyStructureAttributesPacket.CODEC);
	}
	
	private void onStartWorldTick(LevelTickEvent.Pre event) {
		if (event.getLevel() instanceof ServerLevel level) {
			StructureAttributeHandler.tick(level);
		}
	}
	
	private void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			StructureAttributeHandler.disconnect(player);
		}
	}
}
