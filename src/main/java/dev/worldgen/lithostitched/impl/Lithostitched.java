package dev.worldgen.lithostitched.impl;

import dev.worldgen.apollib.Apollib;
import dev.worldgen.apollib.config.ApollibConfigHolder;
import dev.worldgen.apollib.registry.ApollibRegistrar;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.impl.config.ConfigState;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lithostitched {
	public static final String MOD_ID = "lithostitched";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ApollibConfigHolder<ConfigState> CONFIG = Apollib.createConfigHolder(
		id(MOD_ID),
		ApollibConfigHolder.CONFIG_DIRECTORY.resolve("lithostitched.json"),
		ConfigState.CODEC,
		ConfigState.DEFAULT
	);
	public static final ApollibRegistrar REGISTRAR = Apollib.createRegistrar(MOD_ID);
	
	public static void init() {
		CONFIG.load();
		
		LithostitchedBuiltInRegistries.init();
		REGISTRAR.registerAll();
	}
	
	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> resourceKey, String name) {
		return ResourceKey.create(resourceKey, id(name));
	}

	public static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}
	
	public static Identifier vanillaToLithostitched(Identifier id) {
		if (id.getNamespace().equals("minecraft")) {
			return id(id.getPath());
		}
		return id;
	}

	public static <T> Registry<T> registry(RegistryAccess registries, ResourceKey<? extends Registry<T>> key) {
		return registries.lookupOrThrow(key);
	}
	
	public static boolean breaksSeedParity() {
		return CONFIG.getState().breaksSeedParity;
	}

	public static void debug(String message, Object... arguments) {
		if (CONFIG.getState().logDebugMessages) {
			Lithostitched.LOGGER.warn(message, arguments);
		}
	}
}
