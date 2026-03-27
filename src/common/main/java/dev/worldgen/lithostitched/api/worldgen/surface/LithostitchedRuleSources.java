package dev.worldgen.lithostitched.api.worldgen.surface;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.worldgen.surface.rule.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public interface LithostitchedRuleSources {
	static RuleSource bandlands(Holder<Bandlands> bandlands) {
		return new BandlandsRule(bandlands);
	}
	
	static RuleSource reference(Holder<RuleSource> rule) {
		return new ReferenceRule(HolderSet.direct(rule));
	}
	
	static RuleSource reference(HolderSet<RuleSource> rules) {
		return new ReferenceRule(rules);
	}
}
