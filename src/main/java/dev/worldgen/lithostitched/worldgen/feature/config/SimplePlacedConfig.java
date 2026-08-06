package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record SimplePlacedConfig(Holder<PlacedFeature> feature) implements FeatureConfiguration {
	public static final Codec<SimplePlacedConfig> CODEC = PlacedFeature.CODEC.xmap(SimplePlacedConfig::new, SimplePlacedConfig::feature);
}
