package dev.worldgen.lithostitched.api.worldgen.placementcondition;

import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.placementcondition.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface LithostitchedPlacementConditions {
	static PlacementCondition allOf(PlacementCondition... conditions) {
		return new AllOfPlacementCondition(Arrays.asList(conditions));
	}
	
	static PlacementCondition anyOf(PlacementCondition... conditions) {
		return new AnyOfPlacementCondition(Arrays.asList(conditions));
	}
	
	static PlacementCondition grid(int radius, int distBetweenPoints, PlacementCondition condition, InclusiveRange<Integer> allowedCount) {
		return new GridPlacementCondition(radius, distBetweenPoints, condition, allowedCount);
	}
	
	static PlacementCondition absoluteHeightFilter(Optional<Heightmap.Types> heightmap, InclusiveRange<Integer> permittedRange)  {
		return new HeightFilterPlacementCondition(HeightFilterPlacementCondition.RangeType.ABSOLUTE, heightmap, permittedRange);
	}
	
	static PlacementCondition heightmapRelativeFilter(Heightmap.Types heightmap, InclusiveRange<Integer> permittedRange)  {
		return new HeightFilterPlacementCondition(HeightFilterPlacementCondition.RangeType.HEIGHTMAP_RELATIVE, Optional.of(heightmap), permittedRange);
	}
	
	static PlacementCondition inBiome(Holder<Biome> biome) {
		return new InBiomePlacementCondition(HolderSet.direct(biome));
	}
	
	static PlacementCondition inBiome(HolderSet<Biome> biomes) {
		return new InBiomePlacementCondition(biomes);
	}
	
	static PlacementCondition multipleOf(List<PlacementCondition> conditions, InclusiveRange<Integer> allowedCount) {
		return new MultipleOfPlacementCondition(conditions, allowedCount);
	}
	
	static PlacementCondition not(PlacementCondition condition) {
		return new NotPlacementCondition(condition);
	}
	
	static PlacementCondition offset(PlacementCondition condition, BlockPos offset) {
		return new OffsetPlacementCondition(condition, offset);
	}
	
	static PlacementCondition sampleDensity(Holder<DensityFunction> densityFunction, InclusiveRange<Double> range) {
		return new SampleDensityPlacementCondition(densityFunction, range);
	}
	
	static PlacementCondition sampleNoiseRouter(NoiseRouterTarget target, InclusiveRange<Double> range) {
		return new SampleNoiseRouterPlacementCondition(target, range);
	}
	
	static PlacementCondition alwaysTrue() {
		return TruePlacementCondition.INSTANCE;
	}
}
