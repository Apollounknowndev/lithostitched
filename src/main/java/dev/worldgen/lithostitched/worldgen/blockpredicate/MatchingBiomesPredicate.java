package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record MatchingBiomesPredicate(HolderSet<Biome> biomes) implements BlockPredicate {
    public static final MapCodec<MatchingBiomesPredicate> CODEC = Biome.LIST_CODEC.fieldOf("biomes").xmap(MatchingBiomesPredicate::new, MatchingBiomesPredicate::biomes);
    public static final BlockPredicateType<MatchingBiomesPredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        return this.biomes.contains(level.getBiome(pos));
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
