package dev.worldgen.lithostitched.impl;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.msrandom.multiplatform.annotations.Expect;

import java.nio.file.Path;
import java.util.List;

public class LithostitchedPlatform {
	@Expect
	public static boolean isFabric();
	
	@Expect
	public static String getPlatformName();
	
	@Expect
	public static Path getConfigFolder();
	
	@Expect
	public static boolean isModLoaded(String id);
	
	@Expect
	public static void rebuildSettings(Biome biome, List<HolderSet<PlacedFeature>> features);
	
	@Expect
	public static void initPlatformRegistrations();
}
