package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import java.util.List;

public record MultipleOfPredicate(List<BlockPredicate> predicates, InclusiveRange<Integer> allowedCount) implements BlockPredicate {
    public static final MapCodec<MultipleOfPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter(MultipleOfPredicate::predicates),
        InclusiveRange.INT.fieldOf("allowed_count").forGetter(MultipleOfPredicate::allowedCount)
    ).apply(instance, MultipleOfPredicate::new));

    public static final BlockPredicateType<MultipleOfPredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        int count = 0;
        for (BlockPredicate predicate : predicates) {
            if (predicate.test(level, pos)) {
                count++;
                if (this.allowedCount.maxInclusive() < count) return false;
            }
        }
        return this.allowedCount.isValueInRange(count);
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
