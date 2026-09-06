package dev.worldgen.lithostitched.duck;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

public interface ContextBiomeAccessor {
	SurfaceRules.Condition lithostitched$biomeMatches(HolderSet<Biome> biome);
}
