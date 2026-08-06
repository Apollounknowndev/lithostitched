package dev.worldgen.lithostitched.duck;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Set;

public interface ContextBiomeAccessor {
	SurfaceRules.Condition biomeMatches(HolderSet<Biome> biome);
	
	static ContextBiomeAccessor cast(SurfaceRules.Context context) {
		return (ContextBiomeAccessor) (Object) context;
	}
	
	static boolean canNeverMatch(HolderSet<Biome> biomes, Set<Holder<Biome>> possibleBiomes) {
		for(Holder<Biome> biome : biomes) {
			if (possibleBiomes.contains(biome)) {
				return false;
			}
		}
		
		return true;
	}
	
	static boolean willAlwaysMatch(HolderSet<Biome> biomes, Set<Holder<Biome>> possibleBiomes) {
		for(Holder<Biome> possibleBiome : possibleBiomes) {
			if (!biomes.contains(possibleBiome)) {
				return false;
			}
		}
		
		return true;
	}
}
