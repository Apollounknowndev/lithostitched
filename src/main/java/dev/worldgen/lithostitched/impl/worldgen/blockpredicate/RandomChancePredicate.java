package dev.worldgen.lithostitched.impl.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record RandomChancePredicate(float chance) implements BlockPredicate {
    public static final MapCodec<RandomChancePredicate> CODEC = LithostitchedCodecs.CHANCE.xmap(RandomChancePredicate::new, RandomChancePredicate::chance);
    public static final BlockPredicateType<RandomChancePredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        RandomSource random = level.getRandom().forkPositional().at(pos);
        return random.nextFloat() < this.chance;
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
