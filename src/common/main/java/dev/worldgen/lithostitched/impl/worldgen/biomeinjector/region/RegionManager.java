package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

import java.util.*;

public class RegionManager {
	private static final ResourceKey<Region> NO_REGIONS = error("no_regions");
	private static final ResourceKey<Region> NO_REGIONS_IN_RANGE = error("no_regions_in_range");
	
	private final Optional<DensityFunction> regionFunction;
	private final Map<InclusiveRange<Integer>, ResourceKey<Region>> regionsByOutputs;
	private final int totalWeight;
	
	public RegionManager(Optional<DensityFunction> regionFunction, Map<ResourceKey<Region>, Region> regions, DensityFunctionWrapper noiseHelper) {
		this.regionFunction = regionFunction.flatMap(df -> Optional.of(df.mapAll(noiseHelper)));
		this.regionsByOutputs = new HashMap<>();
		int weight = 1;
		for (Map.Entry<ResourceKey<Region>, Region> entry : regions.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().identifier())).toList()) {
			Region region = entry.getValue();
			if (region.weight() <= 0) continue;
			
			var outputRange = InclusiveRange.create(weight, weight + region.weight() - 1).getOrThrow();
			this.regionsByOutputs.put(outputRange, entry.getKey());
			weight += region.weight();
		}
		this.totalWeight = weight - 1;
	}
	
	public int getRegionValue(FunctionContext context) {
		double density = this.regionFunction.get().compute(context);
		return (int) (Math.clamp(density, 0, 1) * totalWeight + 1);
	}
	
	public ResourceKey<Region> getRegion(FunctionContext context) {
		if (this.regionFunction.isEmpty()) return NO_REGIONS;
		
		int value = getRegionValue(context);
		
		for (var entry : this.regionsByOutputs.entrySet()) {
			if (entry.getKey().isValueInRange(value)) {
				return entry.getValue();
			}
		}
		return NO_REGIONS_IN_RANGE;
	}
	
	private static ResourceKey<Region> error(String message) {
		return Lithostitched.key(LithostitchedRegistries.REGION, "error/" + message);
	}
}
