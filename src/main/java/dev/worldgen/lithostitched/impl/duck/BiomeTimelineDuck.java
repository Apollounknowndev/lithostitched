package dev.worldgen.lithostitched.impl.duck;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.timeline.Timeline;

public interface BiomeTimelineDuck {
	void lithostitched$addTimeline(Holder<Timeline> timeline);
	void lithostitched$setTimelines(HolderSet<Timeline> timelines);
	HolderSet<Timeline> lithostitched$getTimelines();
	
	static BiomeTimelineDuck cast(Biome biome) {
		return (BiomeTimelineDuck) (Object) biome;
	}
}
