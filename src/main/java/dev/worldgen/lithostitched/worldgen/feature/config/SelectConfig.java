package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record SelectConfig(List<Pair<BlockPredicate, Holder<PlacedFeature>>> features) implements FeatureConfiguration {
    public static final Codec<Pair<BlockPredicate, Holder<PlacedFeature>>> PAIR_CODEC = Codec.pair(
        BlockPredicate.CODEC.fieldOf("predicate").codec(),
        PlacedFeature.CODEC.fieldOf("feature").codec()
    );

    public static final Codec<SelectConfig> CODEC = PAIR_CODEC.listOf().fieldOf("features").codec().xmap(SelectConfig::new, SelectConfig::features);
}
