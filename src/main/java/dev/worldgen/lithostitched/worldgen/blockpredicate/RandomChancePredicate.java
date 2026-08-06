package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record RandomChancePredicate(float chance) implements BlockPredicate {
    public static final MapCodec<RandomChancePredicate> CODEC = LithostitchedCodecs.CHANCE.xmap(RandomChancePredicate::new, RandomChancePredicate::chance);
    public static final BlockPredicateType<RandomChancePredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        RandomSource random = RandomSource.create(level.getSeed()).forkPositional().at(pos);
        return random.nextFloat() < this.chance;
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
