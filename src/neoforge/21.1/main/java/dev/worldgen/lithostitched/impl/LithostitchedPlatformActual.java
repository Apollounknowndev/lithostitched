package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.registry.LithostitchedNeoforgeBiomeModifiers;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LithostitchedPlatformActual {
	@Actual
	public static boolean isFabric() {
		return false;
	}
	
	@Actual
	public static String getPlatformName() {
		return "neoforge";
	}
	
	@Actual
	public static Path getConfigFolder() {
		return FMLPaths.CONFIGDIR.get();
	}
	
	@Actual
	public static boolean isModLoaded(String id) {
		return ModList.get().isLoaded(id);
	}
	
	@Actual
	public static void rebuildSettings(Biome biome, List<HolderSet<PlacedFeature>> features) {
		throw new IllegalStateException("Cannot call LithostitchedPlatform#rebuildSettings on Neoforge.");
	}
	
	@Actual
	public static void initPlatformRegistrations() {
		LithostitchedRegistrar.register(NeoForgeRegistries.Keys.CONDITION_CODECS, Map.ofEntries(
			Map.entry("breaks_seed_parity", BreaksSeedParityCondition.CODEC)
		));
		LithostitchedRegistrar.register(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Map.ofEntries(
			Map.entry("replace_climate", LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC),
			Map.entry("replace_effects", LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC)
		));
	}
	
	@Actual
	public static <T> Supplier<T> memoize(Supplier<T> delegate) {
		return Lazy.of(delegate);
	}
}
