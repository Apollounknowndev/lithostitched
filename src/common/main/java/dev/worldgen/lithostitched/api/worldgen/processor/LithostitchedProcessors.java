package dev.worldgen.lithostitched.api.worldgen.processor;

import dev.worldgen.lithostitched.api.util.WeightedList;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.RandomMode;
import dev.worldgen.lithostitched.worldgen.processor.*;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import java.util.List;
import java.util.Map;

import static dev.worldgen.lithostitched.worldgen.processor.ApplyRandomStructureProcessor.convertToSet;

public interface LithostitchedProcessors {
	static StructureProcessor applyRandom(HolderSet<StructureProcessorList> lists, RandomSettings settings) {
		return new ApplyRandomStructureProcessor(lists, settings);
	}
	
	static StructureProcessor applyRandom(WeightedList<Holder<StructureProcessorList>> lists, RandomSettings settings) {
		return new ApplyRandomStructureProcessor(convertToSet(lists), settings);
	}
	
	static StructureProcessor blockSwap(Map<ResourceKey<Block>, ResourceKey<Block>> blocks) {
		return new BlockSwapStructureProcessor(blocks);
	}
	
	static StructureProcessor condition(RandomSettings randomSettings, ProcessorCondition ifTrue, StructureProcessor... thenRun) {
		return new ConditionProcessor(randomSettings, ifTrue, List.of(thenRun), List.of());
	}
	
	static StructureProcessor condition(RandomSettings randomSettings, ProcessorCondition ifTrue, List<StructureProcessor> thenRun, List<StructureProcessor> elseRun) {
		return new ConditionProcessor(randomSettings, ifTrue, thenRun, elseRun);
	}
	
	static StructureProcessor discardInput() {
		return new DiscardInputProcessor();
	}
	
	static StructureProcessor reference(Holder<StructureProcessorList> list) {
		return new ReferenceStructureProcessor(HolderSet.direct(list));
	}
	
	static StructureProcessor reference(Holder<StructureProcessorList> list) {
		return new ReferenceStructureProcessor(HolderSet.direct(list));
	}
	
	static StructureProcessor reference(HolderSet<StructureProcessorList> lists) {
		return new ReferenceStructureProcessor(lists);
	}
	
	static StructureProcessor scheduleTick() {
		return ScheduleTickProcessor.INSTANCE;
	}
	
	static StructureProcessor setBlock(BlockStateProvider stateProvider, boolean preserveState, RandomMode randomMode, RuleBlockEntityModifier modifier) {
		return new SetBlockProcessor(stateProvider, preserveState, randomMode, modifier);
	}
}
