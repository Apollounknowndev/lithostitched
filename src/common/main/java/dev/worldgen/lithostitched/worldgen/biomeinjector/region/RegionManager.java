package dev.worldgen.lithostitched.worldgen.biomeinjector.region;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RegionManager {
	private static final ResourceKey<Region> NO_REGIONS = error("no_regions");
	private static final ResourceKey<Region> REGION_NAME_MISSING = error("region_name_missing");
	private static final ResourceKey<Region> NO_REGIONS_IN_RANGE = error("no_regions_in_range");
	
	
	private final Optional<DensityFunction> regionFunction;
	private final Map<InclusiveRange<Integer>, Holder<Region>> regionsByOutputs;
	private final int totalWeight;
	
	public RegionManager(Optional<DensityFunction> regionFunction, List<Holder<Region>> regions, NoiseWiringHelper noiseHelper) {
		this.regionFunction = regionFunction.flatMap(df -> Optional.of(df.mapAll(noiseHelper)));
		this.regionsByOutputs = new HashMap<>();
		int weight = 1;
		for (Holder<Region> region : regions) {
			this.regionsByOutputs.put(InclusiveRange.create(weight, weight + region.value().weight() - 1).getOrThrow(), region);
			weight += region.value().weight();
		}
		this.totalWeight = weight - 1;
	}
	
	public int getRegionValue(DensityFunction.SinglePointContext context) {
		double density = this.regionFunction.get().compute(context);
		return (int) (Math.clamp(density, 0, 1) * totalWeight + 1);
	}
	
	public ResourceKey<Region> getRegion(DensityFunction.SinglePointContext context) {
		if (this.regionFunction.isEmpty()) return NO_REGIONS;
		
		int value = getRegionValue(context);
		
		for (var entry : this.regionsByOutputs.entrySet()) {
			if (entry.getKey().isValueInRange(value)) {
				Holder<Region> region = entry.getValue();
				if (region.unwrapKey().isPresent()) {
					return region.unwrapKey().get();
				}
				if (region.value().name().isPresent()) {
					return region.value().name().get();
				}
				return REGION_NAME_MISSING;
			}
		}
		return NO_REGIONS_IN_RANGE;
	}
	
	private static ResourceKey<Region> error(String message) {
		return Lithostitched.key(LithostitchedRegistries.REGION, "error/" + message);
	}
}
