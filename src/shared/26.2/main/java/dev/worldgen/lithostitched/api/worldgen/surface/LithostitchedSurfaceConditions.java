package dev.worldgen.lithostitched.api.worldgen.surface;

import dev.worldgen.lithostitched.impl.worldgen.surface.condition.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;

import java.util.Arrays;

public interface LithostitchedSurfaceConditions {
	static ConditionSource allOf(ConditionSource... conditions) {
		return new AllOfCondition(Arrays.asList(conditions));
	}
	
	static ConditionSource anyOf(ConditionSource... conditions) {
		return new AnyOfCondition(Arrays.asList(conditions));
	}
	
	static ConditionSource biome(HolderSet<Biome> biomes) {
		return new BiomeCondition(biomes);
	}
	
	static ConditionSource slope(InclusiveRange<Integer> threshold) {
		return new SlopeCondition(threshold);
	}
	
	static ConditionSource sampleDensity(DensityFunction densityFunction, InclusiveRange<Double> range) {
		return new SampleDensityCondition(densityFunction, range);
	}
}
