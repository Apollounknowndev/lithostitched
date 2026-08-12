package dev.worldgen.lithostitched.api.worldgen.surface;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public interface LithostitchedSurfaceRules {
	static RuleSource bandlands(Holder<Bandlands> bandlands) {
		return new BandlandsRule(bandlands);
	}
}
