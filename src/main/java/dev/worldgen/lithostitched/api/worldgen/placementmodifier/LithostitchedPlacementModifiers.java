package dev.worldgen.lithostitched.api.worldgen.placementmodifier;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementmodifier.ConditionPlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.NoiseSlopePlacement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface LithostitchedPlacementModifiers {
	static PlacementModifier condition(PlacementCondition condition) {
		return new ConditionPlacement(condition);
	}
	
	static PlacementModifier noiseSlope(ResourceKey<NormalNoise> noise, int slope, int offset, double xzScale, double yScale) {
		return new NoiseSlopePlacement(noise, slope, offset, xzScale, yScale);
	}
}
