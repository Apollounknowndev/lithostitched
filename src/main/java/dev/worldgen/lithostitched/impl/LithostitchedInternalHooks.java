package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CellularDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;

public class LithostitchedInternalHooks {
	public static void onServerAboutToStart(MinecraftServer server) {
		RegistryAccess registries = server.registryAccess();
		long seed = server.getWorldGenSettings().options().seed();
		
		applyModifiersAndInjections(registries, Lithostitched.registry(registries, Registries.LEVEL_STEM), seed);
	}
	
	public static void applyModifiersAndInjections(RegistryAccess registries, Registry<LevelStem> dimensions, long seed) {
		for (Holder.Reference<DensityFunction> df : Lithostitched.registry(registries, Registries.DENSITY_FUNCTION).listElements().toList()) {
			var accessor = ((HolderReferenceAccessor<DensityFunction>)df);
			accessor.setValue(
				df.value().mapAll(input -> input instanceof CellularDensityFunction cellular ? cellular.withSeed(seed) : input)
			);
		}
		
		ModifierManager.applyModifiers(registries, dimensions);
		SurfaceRuleManager.applySurfaceRules(registries, dimensions);
		BiomeInjectorManager.applyBiomeInjectors(registries, dimensions, seed);
		
		for (Holder<FastNoiseConfig> config : registries.lookupOrThrow(LithostitchedRegistries.FAST_NOISE_CONFIG).listElements().toList()) {
			config.value().bind(seed);
		}
	}
}
