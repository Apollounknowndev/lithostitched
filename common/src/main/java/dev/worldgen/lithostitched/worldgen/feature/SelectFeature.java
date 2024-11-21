package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.worldgen.feature.config.SelectConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SelectFeature extends Feature<SelectConfig> {
    public static SelectFeature FEATURE = new SelectFeature();
    public SelectFeature() {
        super(SelectConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<SelectConfig> context) {
        SelectConfig config = context.config();
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        for (Pair<BlockPredicate, Holder<PlacedFeature>> pair : config.features()) {
            if (pair.getFirst().test(level, origin)) {
                pair.getSecond().value().place(level, generator, random, origin);
                return true;
            }
        }

        return false;
    }
}
