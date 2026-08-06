package dev.worldgen.lithostitched.api.worldgen.placementmodifier;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementmodifier.ConditionPlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.NoiseSlopePlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.OffsetPlacement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface LithostitchedPlacementModifiers {
	static PlacementModifier condition(PlacementCondition condition) {
		return new ConditionPlacement(condition);
	}
	
	static PlacementModifier noiseSlope(ResourceKey<NormalNoise.NoiseParameters> noise, int slope, int offset, double xzScale, double yScale) {
		return new NoiseSlopePlacement(noise, slope, offset, xzScale, yScale);
	}
	
	static PlacementModifier offset(IntProvider xOffset, IntProvider yOffset, IntProvider zOffset) {
		return new OffsetPlacement(xOffset, yOffset, zOffset);
	}
}
