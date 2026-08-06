package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CompositeFeature extends Feature<CompositeConfig> {
    public static final CompositeFeature FEATURE = new CompositeFeature(CompositeConfig.CODEC);
    public CompositeFeature(Codec<CompositeConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CompositeConfig> context) {
        CompositeConfig config = context.config();
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean anyPlaced = false;

        for (Holder<PlacedFeature> feature : config.features()) {
            boolean placed = feature.value().place(level, generator, random, origin);

            if (placed) {
                anyPlaced = true;
            }

            if (!config.placementType().shouldContinue(placed)) {
                break;
            }
        }

        return anyPlaced;
    }
}
