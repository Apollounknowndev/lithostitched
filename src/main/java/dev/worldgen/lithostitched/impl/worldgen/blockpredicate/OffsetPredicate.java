package dev.worldgen.lithostitched.impl.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record OffsetPredicate(BlockPredicate predicate, Vec3i offset) implements BlockPredicate {
    public static final MapCodec<OffsetPredicate> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BlockPredicate.CODEC.fieldOf("predicate").forGetter(OffsetPredicate::predicate),
        Vec3i.offsetCodec(16).fieldOf("offset").forGetter(OffsetPredicate::offset)
    ).apply(i, OffsetPredicate::new));
    public static final BlockPredicateType<OffsetPredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        return this.predicate.test(level, pos.offset(this.offset));
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
