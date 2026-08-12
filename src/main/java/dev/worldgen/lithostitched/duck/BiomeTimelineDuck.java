package dev.worldgen.lithostitched.impl.duck;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.timeline.Timeline;

public interface BiomeTimelineDuck {
	void lithostitched$setTimeline(Holder<Timeline> timeline);
	Holder<Timeline> lithostitched$getTimeline();
	
	static BiomeTimelineDuck cast(Biome biome) {
		return (BiomeTimelineDuck) (Object) biome;
	}
}
