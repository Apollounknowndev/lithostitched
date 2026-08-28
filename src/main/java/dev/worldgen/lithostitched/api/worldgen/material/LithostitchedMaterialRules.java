package dev.worldgen.lithostitched.api.worldgen.material;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.material.rule.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public interface LithostitchedMaterialRules {
	static MaterialRule bandlands(Holder<Bandlands> bandlands) {
		return new BandlandsRule(bandlands);
	}
}
