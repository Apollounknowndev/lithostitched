package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record LargeDripstoneConfig(BlockStateProvider stateProvider, HolderSet<Block> replaceableBlocks, int floorToCeilingSearchRange, IntProvider columnRadius, FloatProvider heightScale, float maxColumnRadiusToCaveHeightRatio, FloatProvider stalactiteBluntness, FloatProvider stalagmiteBluntness, FloatProvider windSpeed, int minRadiusForWind, float minBluntnessForWind) implements FeatureConfiguration {
    public static final Codec<LargeDripstoneConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(LargeDripstoneConfig::stateProvider),
        LithostitchedCodecs.BLOCK_SET.fieldOf("replaceable_blocks").forGetter(LargeDripstoneConfig::replaceableBlocks),
        Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter(LargeDripstoneConfig::floorToCeilingSearchRange),
        IntProvider.codec(1, 60).fieldOf("column_radius").forGetter(LargeDripstoneConfig::columnRadius),
        FloatProvider.codec(0.0F, 20.0F).fieldOf("height_scale").forGetter(LargeDripstoneConfig::heightScale),
        Codec.floatRange(0.1F, 1.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter(LargeDripstoneConfig::maxColumnRadiusToCaveHeightRatio),
        FloatProvider.codec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter(LargeDripstoneConfig::stalactiteBluntness),
        FloatProvider.codec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter(LargeDripstoneConfig::stalagmiteBluntness),
        FloatProvider.codec(0.0F, 2.0F).fieldOf("wind_speed").forGetter(LargeDripstoneConfig::windSpeed),
        Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(LargeDripstoneConfig::minRadiusForWind),
        Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(LargeDripstoneConfig::minBluntnessForWind)
    ).apply(instance, LargeDripstoneConfig::new));
}

