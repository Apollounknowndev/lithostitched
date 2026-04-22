package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;

public class LithostitchedInternalHooks {
	public static void onServerAboutToStart(MinecraftServer server) {
		ModifierManager.applyModifiers(server);
		SurfaceRuleManager.applySurfaceRules(server);
		BiomeInjectorManager.applyBiomeInjectors(server);
		
		long seed = LithostitchedVersion.getSeed(server);
		for (Holder<FastNoiseConfig> config : server.registryAccess().lookupOrThrow(LithostitchedRegistries.FAST_NOISE_CONFIG).listElements().toList()) {
			config.value().bind(seed);
		}
	}
}
