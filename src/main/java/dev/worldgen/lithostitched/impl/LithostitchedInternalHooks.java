package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.impl.worldgen.surface.MaterialRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;

public class LithostitchedInternalHooks {
	public static void onServerAboutToStart(MinecraftServer server) {
		RegistryAccess registries = server.registryAccess();
		long seed = server.getWorldGenSettings().options().seed();
		
		applyModifiersAndInjections(registries, Lithostitched.registry(registries, Registries.LEVEL_STEM), seed);
	}
	
	public static void applyModifiersAndInjections(RegistryAccess registries, Registry<LevelStem> dimensions, long seed) {
		ModifierManager.applyModifiers(registries, dimensions);
		MaterialRuleManager.applySurfaceRules(registries, dimensions);
		BiomeInjectorManager.applyBiomeInjectors(registries, dimensions, seed);
		
		for (Holder<FastNoiseConfig> config : registries.lookupOrThrow(LithostitchedRegistries.FAST_NOISE_CONFIG).listElements().toList()) {
			config.value().bind(seed);
		}
	}
}
