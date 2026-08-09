package dev.worldgen.lithostitched.api.worldgen.blockpredicate;

import dev.worldgen.lithostitched.impl.worldgen.blockpredicate.*;
import dev.worldgen.lithostitched.api.util.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Arrays;
import java.util.Optional;

public interface LithostitchedBlockPredicates {
	static BlockPredicate blockState(StatePropertiesPredicate predicate) {
		return new BlockStatePredicate(Vec3i.ZERO, predicate);
	}
	
	static BlockPredicate blockState(Vec3i offset, StatePropertiesPredicate predicate) {
		return new BlockStatePredicate(offset, predicate);
	}
	
	static BlockPredicate inStructure(int searchRange) {
		return new InStructurePredicate(Optional.empty(), new InStructurePredicate.SearchRange(searchRange));
	}
	
	static BlockPredicate inStructure(int searchRangeXZ, int searchRangeY) {
		return new InStructurePredicate(Optional.empty(), new InStructurePredicate.SearchRange(searchRangeXZ, searchRangeY));
	}
	
	static BlockPredicate inStructure(Holder<Structure> structure, int searchRange) {
		return new InStructurePredicate(Optional.of(structure), new InStructurePredicate.SearchRange(searchRange));
	}
	
	static BlockPredicate inStructure(Holder<Structure> structure, int searchRangeXZ, int searchRangeY) {
		return new InStructurePredicate(Optional.of(structure), new InStructurePredicate.SearchRange(searchRangeXZ, searchRangeY));
	}
	
	static BlockPredicate multipleOf(InclusiveRange<Integer> allowedCount, BlockPredicate... predicates) {
		return new MultipleOfPredicate(Arrays.asList(predicates), allowedCount);
	}
	
	static BlockPredicate offset(BlockPredicate predicate, Vec3i offset) {
		return new OffsetPredicate(predicate, offset);
	}
	
	static BlockPredicate randomChance(float chance) {
		return new RandomChancePredicate(chance);
	}
}
