package dev.worldgen.lithostitched;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedRegistrations;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
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
	}
	
	public static <T> Consumer<DataPackRegistryEvent.NewRegistry> registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		return event -> event.dataPackRegistry(key, codec);
	}
	
	public static <T> Consumer<RegistryBuilder<T>> emptyConsumer() {
		return b -> {};
	}
}
