package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.WeightedSelectorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class WeightedSelectorFeature extends Feature<WeightedSelectorConfig> {
    public static final WeightedSelectorFeature FEATURE = new WeightedSelectorFeature();

    public WeightedSelectorFeature() {
        super(WeightedSelectorConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<WeightedSelectorConfig> context) {
        WeightedSelectorConfig config = context.config();
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        var feature = config.features().getRandomValue(random);
        return feature.map(placedFeatureHolder -> placedFeatureHolder.value().place(level, generator, random, origin)).orElse(false);
    }
}
