package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

import java.util.*;

public class RegionManager {
	private static final ResourceKey<Region> NO_REGIONS = error("no_regions");
	private static final ResourceKey<Region> NO_REGIONS_IN_RANGE = error("no_regions_in_range");
	
	private final Optional<DensityFunction> regionFunction;
	private final Map<Holder<Biome>, BiomeRegions> regionsByBiome;
	
	public RegionManager(Optional<DensityFunction> regionFunction, Map<ResourceKey<Region>, Region> regions, DensityFunctionWrapper noiseHelper, Collection<Holder<Biome>> biomes) {
		this.regionFunction = regionFunction.flatMap(df -> Optional.of(df.mapAll(noiseHelper)));
		this.regionsByBiome = new HashMap<>();
		
		for (Holder<Biome> biome : biomes) {
			TreeMap<Integer, ResourceKey<Region>> biomeMap = new TreeMap<>();
			int weight = 1;
			for (Map.Entry<ResourceKey<Region>, Region> entry : regions.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().identifier())).toList()) {
				Region region = entry.getValue();
				if (!region.biomes().contains(biome)) continue;
				int regionWeight = region.weight();
				if (regionWeight <= 0) continue;
				
				biomeMap.put(weight, entry.getKey());
				weight += regionWeight;
			}
			this.regionsByBiome.put(biome, new BiomeRegions(biomeMap, weight - 1));
		}
	}
	
	public ResourceKey<Region> getRegion(FunctionContext context, Holder<Biome> biome) {
		BiomeRegions biomeRegions = this.regionsByBiome.get(biome);
		if (biomeRegions == null) return NO_REGIONS;
		
		return biomeRegions.getRegion(regionFunction, context);
	}
	
	public int getRegionValue(FunctionContext context, Holder<Biome> biome) {
		BiomeRegions biomeRegions = this.regionsByBiome.get(biome);
		if (biomeRegions == null || regionFunction.isEmpty()) return -1;
		
		return biomeRegions.getRegionValue(regionFunction.get(), context);
	}
	
	private static ResourceKey<Region> error(String message) {
		return Lithostitched.key(LithostitchedRegistries.REGION, "error/" + message);
	}
	
	private record BiomeRegions(TreeMap<Integer, ResourceKey<Region>> regionsByOutputs, int totalWeight) {
		public int getRegionValue(DensityFunction regionFunction, FunctionContext context) {
			double density = regionFunction.compute(context);
			return (int) (Math.clamp(density, 0, 1) * totalWeight + 1);
		}
		
		public ResourceKey<Region> getRegion(Optional<DensityFunction> regionFunction, FunctionContext context) {
			if (regionFunction.isEmpty() || regionsByOutputs.isEmpty()) return NO_REGIONS;
			
			int value = getRegionValue(regionFunction.get(), context);
			
			var entry = regionsByOutputs.floorEntry(value);
			if (entry == null) return NO_REGIONS_IN_RANGE;
			
			return entry.getValue();
		}
	}
}