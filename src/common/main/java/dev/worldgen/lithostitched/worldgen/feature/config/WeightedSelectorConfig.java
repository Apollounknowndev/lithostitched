package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.util.WeightedList;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WeightedSelectorConfig(WeightedList<Holder<PlacedFeature>> features) implements FeatureConfiguration {

    public static final Codec<WeightedSelectorConfig> CODEC = WeightedList.codec(PlacedFeature.CODEC).fieldOf("features").codec().xmap(WeightedSelectorConfig::new, WeightedSelectorConfig::features);
}
