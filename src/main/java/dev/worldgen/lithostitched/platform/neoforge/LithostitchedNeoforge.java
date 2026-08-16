//? if neoforge {
package dev.worldgen.lithostitched.platform.neoforge;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.function.Consumer;

@Mod(Lithostitched.MOD_ID)
public final class LithostitchedNeoforge {
	public LithostitchedNeoforge(IEventBus bus) {
		Lithostitched.init();
		LithostitchedRegistrations.init(bus);
	}
	
	public static <T> Consumer<DataPackRegistryEvent.NewRegistry> registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		return event -> event.dataPackRegistry(key, codec);
	}
}
//? }