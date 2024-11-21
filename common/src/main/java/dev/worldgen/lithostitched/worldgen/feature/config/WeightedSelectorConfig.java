package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WeightedSelectorConfig(SimpleWeightedRandomList<Holder<PlacedFeature>> features) implements FeatureConfiguration {

    public static final Codec<WeightedSelectorConfig> CODEC = SimpleWeightedRandomList.wrappedCodec(PlacedFeature.CODEC).fieldOf("features").codec().xmap(WeightedSelectorConfig::new, WeightedSelectorConfig::features);
}
