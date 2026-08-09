package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record SelectFeature(List<Pair<BlockPredicate, Holder<PlacedFeature>>> features) implements Feature {
    public static final Codec<Pair<BlockPredicate, Holder<PlacedFeature>>> PAIR_CODEC = Codec.pair(
        BlockPredicate.CODEC.fieldOf("predicate").codec(),
        PlacedFeature.CODEC.fieldOf("feature").codec()
    );
    
    public static final MapCodec<SelectFeature> CODEC = PAIR_CODEC.listOf().fieldOf("features").xmap(SelectFeature::new, SelectFeature::features);
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        for (Pair<BlockPredicate, Holder<PlacedFeature>> pair : this.features()) {
            if (pair.getFirst().test(level, origin)) {
                pair.getSecond().value().place(level, generator, random, origin);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}
