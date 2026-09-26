package dev.worldgen.lithostitched.impl.worldgen.blockpredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record HolderHolderPredicate(Holder<BlockPredicate> predicate) implements BlockPredicate {
	@Override
	public BlockPredicateType<?> type() {
		throw new UnsupportedOperationException("HolderHolder cannot be serialized");
	}
	
	@Override
	public boolean test(LevelAccessor level, BlockPos pos) {
		return this.predicate.value().test(level, pos);
	}
}
