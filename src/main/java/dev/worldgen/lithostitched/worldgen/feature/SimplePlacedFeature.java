package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.SimplePlacedConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SimplePlacedFeature extends Feature<SimplePlacedConfig> {
	public static SimplePlacedFeature FEATURE = new SimplePlacedFeature();
	public SimplePlacedFeature() {
		super(SimplePlacedConfig.CODEC);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<SimplePlacedConfig> context) {
		return context.config().feature().value().place(context.level(), context.chunkGenerator(), context.random(), context.origin());
	}
}
