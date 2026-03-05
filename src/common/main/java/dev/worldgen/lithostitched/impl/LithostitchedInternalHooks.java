package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;

public class LithostitchedInternalHooks {
	public static void onServerAboutToStart(MinecraftServer server) {
		ModifierManager.applyModifiers(server);
		SurfaceRuleManager.applySurfaceRules(server);
		BiomeInjectorManager.applyBiomeInjectors(server);
	}
}
