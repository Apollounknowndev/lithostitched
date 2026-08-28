package dev.worldgen.lithostitched.api.worldgen.material;

import dev.worldgen.lithostitched.impl.worldgen.material.condition.*;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

import java.util.Arrays;

public interface LithostitchedMaterialConditions {
	static MaterialCondition allOf(MaterialCondition... conditions) {
		return new AllOfCondition(Arrays.asList(conditions));
	}
	
	static MaterialCondition anyOf(MaterialCondition... conditions) {
		return new AnyOfCondition(Arrays.asList(conditions));
	}
	
	static MaterialCondition slope(InclusiveRange<Integer> threshold) {
		return new SlopeCondition(threshold);
	}
	
	static MaterialCondition sampleDensity(DensityFunction densityFunction, InclusiveRange<Float> range) {
		return new SampleDensityCondition(densityFunction, range);
	}
}
