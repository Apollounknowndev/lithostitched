package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeGenerationSettingsAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.msrandom.multiplatform.annotations.Actual;

import java.nio.file.Path;
import java.util.List;

public class LithostitchedPlatformActual {
	@Actual
	public static boolean isFabric() {
		return true;
	}
	
	@Actual
	public static String getPlatformName() {
		return "fabric";
	}
	
	@Actual
	public static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir();
	}
	
	@Actual
	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}
	
	@Actual
	public static void rebuildSettings(Biome biome, List<HolderSet<PlacedFeature>> features) {
		((BiomeAccessor) (Object) biome).setGenerationSettings(BiomeGenerationSettingsAccessor.createGenerationSettings(
			((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
			features
		));
	}
	
	@Actual
	public static void initPlatformRegistrations() {
	
	}
}
