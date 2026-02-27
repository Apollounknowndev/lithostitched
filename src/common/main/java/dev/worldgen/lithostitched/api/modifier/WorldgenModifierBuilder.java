package dev.worldgen.lithostitched.api.modifier;

import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import java.util.List;

public class WorldgenModifierBuilder {
	public WorldgenModifier addSurfaceRule(ResourceKey<LevelStem> dimension, RuleSource ruleSource) {
		return addSurfaceRule(WorldgenModifier.DEFAULT_PRIORITY, dimension, ruleSource);
	}
	
	public WorldgenModifier addSurfaceRule(int priority, ResourceKey<LevelStem> dimension, RuleSource ruleSource) {
		return new AddSurfaceRuleModifier(priority, List.of(dimension), ruleSource);
	}
}
