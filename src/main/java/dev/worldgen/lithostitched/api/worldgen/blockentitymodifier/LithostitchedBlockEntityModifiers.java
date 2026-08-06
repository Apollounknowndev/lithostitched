package dev.worldgen.lithostitched.api.worldgen.blockentitymodifier;

import dev.worldgen.lithostitched.api.util.WeightedList;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import java.util.Arrays;

public interface LithostitchedBlockEntityModifiers {
	static RuleBlockEntityModifier applyAll(RuleBlockEntityModifier... modifiers) {
		return new ApplyAll(Arrays.stream(modifiers).toList());
	}
	
	static RuleBlockEntityModifier applyRandom(WeightedList<RuleBlockEntityModifier> modifiers) {
		return new ApplyRandom(modifiers);
	}
}
