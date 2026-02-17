package dev.worldgen.lithostitched.worldgen;

import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;

public class LithostitchedEvents {
	public static void onServerAboutToStart(MinecraftServer server) {
		ModifierManager.applyModifiers(server);
		SurfaceRuleManager.applySurfaceRules(server);
		BiomeInjectorManager.applyBiomeInjectors(server);
		
		long seed = server.getWorldData().worldGenOptions().seed();
		for (Holder.Reference<FastNoiseConfig> config : server.registryAccess().lookupOrThrow(LithostitchedRegistryKeys.FAST_NOISE_CONFIG).listElements().toList()) {
			config.value().bind(seed);
		}
	}
}
