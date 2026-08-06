package dev.worldgen.lithostitched.api.worldgen.processorcondition;

import dev.worldgen.lithostitched.api.worldgen.processor.enums.BlockType;
import dev.worldgen.lithostitched.worldgen.processor.condition.*;
import dev.worldgen.lithostitched.api.util.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;

import java.util.Arrays;

public interface LithostitchedProcessorConditions {
	static ProcessorCondition allOf(ProcessorCondition... conditions) {
		return new AllOf(Arrays.asList(conditions));
	}
	
	static ProcessorCondition anyOf(ProcessorCondition... conditions) {
		return new AnyOf(Arrays.asList(conditions));
	}
	
	static ProcessorCondition matchingBlocks(Block block, StatePropertiesPredicate properties, BlockType matchType) {
		return new MatchingBlocks(HolderSet.direct(block.builtInRegistryHolder()), properties, matchType);
	}
	
	static ProcessorCondition matchingBlocks(Holder<Block> block, StatePropertiesPredicate properties, BlockType matchType) {
		return new MatchingBlocks(HolderSet.direct(block), properties, matchType);
	}
	
	static ProcessorCondition matchingBlocks(HolderSet<Block> blocks, StatePropertiesPredicate properties, BlockType matchType) {
		return new MatchingBlocks(blocks, properties, matchType);
	}
	
	static ProcessorCondition not(ProcessorCondition condition) {
		return new Not(condition);
	}
	
	static ProcessorCondition piecePosition(PosRuleTest predicate) {
		return new Position(predicate, Position.PosAnchor.PIECE);
	}
	
	static ProcessorCondition structureStartPosition(PosRuleTest predicate) {
		return new Position(predicate, Position.PosAnchor.STRUCTURE_START);
	}
	
	static ProcessorCondition randomChance(float chance) {
		return new RandomChance(chance);
	}
	
	static ProcessorCondition alwaysTrue() {
		return new True();
	}
}
