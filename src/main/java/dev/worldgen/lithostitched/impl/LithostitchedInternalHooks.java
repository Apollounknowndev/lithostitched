package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CellularDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.modifier.internal.ModifierManager;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

public class LithostitchedInternalHooks {
	public static long WORLD_SEED = 0;
	
	public static void onServerAboutToStart(MinecraftServer server) {
		RegistryAccess registries = server.registryAccess();
		long seed = server.getWorldGenSettings().options().seed();
		
		applyModifiersAndInjections(server, registries, Lithostitched.registry(registries, Registries.LEVEL_STEM), seed);
	}
	
	public static void applyModifiersAndInjections(MinecraftServer server, RegistryAccess registries, Registry<LevelStem> dimensions, long seed) {
		WORLD_SEED = seed;
		for (Holder.Reference<DensityFunction> df : Lithostitched.registry(registries, Registries.DENSITY_FUNCTION).listElements().toList()) {
			var accessor = ((HolderReferenceAccessor<DensityFunction>)df);
			accessor.setValue(
				df.value().rewriteChildren(new SeededRewriteRule(seed))
			);
		}
		
		ModifierManager.applyModifiers(registries, dimensions);
		BiomeInjectorManager.applyBiomeInjectors(server, registries, dimensions, seed);
		
		for (Holder<FastNoiseConfig> config : registries.lookupOrThrow(LithostitchedRegistries.FAST_NOISE_CONFIG).listElements().toList()) {
			config.value().bind(seed);
		}
	}
	
	public record SeededRewriteRule(long seed) implements DfRewriteRule {
		@Override
		public DensityFunction rewrite(DensityFunction input) {
			if (input instanceof CellularDensityFunction cellular) {
				return cellular.withSeed(seed);
			}
			return input;
		}
	}
}
