//? if neoforge {
/*package dev.worldgen.lithostitched.platform.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static dev.worldgen.lithostitched.Lithostitched.MOD_ID;

/^*
 * Built-in registries for Lithostitched on Neoforge.
 ^/
public final class LithostitchedRegistrations {
	public static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTER_CACHE = new HashMap<>();
	public static final List<Consumer<DataPackRegistryEvent.NewRegistry>> DYNAMIC_REGISTRIES = new ArrayList<>();
	
	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<Registry<T>> key) {
		var register = DeferredRegister.create(key, MOD_ID);
		REGISTER_CACHE.put(key, register);
		return register;
	}
	
	public static void init(IEventBus bus) {
		REGISTER_CACHE.values().forEach(deferredRegistry -> deferredRegistry.register(bus));
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			DYNAMIC_REGISTRIES.forEach(consumer -> consumer.accept(event));
		});
	}
}
*///? }