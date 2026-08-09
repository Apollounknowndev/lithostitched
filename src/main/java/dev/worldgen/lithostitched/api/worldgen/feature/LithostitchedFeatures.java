package dev.worldgen.lithostitched.api.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.impl.worldgen.feature.*;
import net.minecraft.util.random.WeightedList;
import dev.worldgen.lithostitched.impl.worldgen.feature.CompositeFeature.Type;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

public interface LithostitchedFeatures {
	static Feature placeAll(HolderSet<PlacedFeature> features) {
		return new CompositeFeature(features, Type.NEVER_CANCEL);
	}
	
	static Feature placeUntilFailure(HolderSet<PlacedFeature> features) {
		return new CompositeFeature(features, Type.CANCEL_ON_FAILURE);
	}
	
	static Feature placeUntilSuccess(HolderSet<PlacedFeature> features) {
		return new CompositeFeature(features, Type.CANCEL_ON_SUCCESS);
	}
	
	static Feature dungeon(int minOpenings, int maxOpenings, IntProvider radius, int maxChests, WeightedList<EntityType<?>> spawnerMobs, BlockStateProvider floorProvider, BlockStateProvider wallProvider, Optional<HolderSet<Block>> dungeonInvalidBlocks, ResourceKey<LootTable> lootTable) {
		return new DungeonFeature(minOpenings, maxOpenings, radius, maxChests, spawnerMobs, floorProvider, wallProvider, dungeonInvalidBlocks, lootTable);
	}
	
	static Feature largeDripstone(BlockStateProvider stateProvider, HolderSet<Block> replaceableBlocks, int floorToCeilingSearchRange, IntProvider columnRadius, FloatProvider heightScale, float maxColumnRadiusToCaveHeightRatio, FloatProvider stalactiteBluntness, FloatProvider stalagmiteBluntness, FloatProvider windSpeed, int minRadiusForWind, float minBluntnessForWind) {
		return new LargeDripstoneFeature(stateProvider, replaceableBlocks, floorToCeilingSearchRange, columnRadius, heightScale, maxColumnRadiusToCaveHeightRatio, stalactiteBluntness, stalagmiteBluntness, windSpeed, minRadiusForWind, minBluntnessForWind);
	}
	
	static Feature ore(int size, List<Pair<BlockPredicate, BlockStateProvider>> targets) {
		return OreFeature.create(size, targets);
	}
	
	static Feature placed(Holder<PlacedFeature> feature) {
		return new SimplePlacedFeature(feature);
	}
	
	static Feature select(List<Pair<BlockPredicate, Holder<PlacedFeature>>> features) {
		return new SelectFeature(features);
	}
	
	static Feature structureTemplate(Identifier template, Holder<StructureProcessorList> processors, LiquidSettings liquidSettings) {
		return new StructureTemplateFeature(template, processors, Optional.empty(), liquidSettings, Optional.empty());
	}
	
	static Feature structureTemplate(Identifier template, Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings, Optional<Identifier> startJigsawName) {
		return new StructureTemplateFeature(template, processors, rotation, liquidSettings, startJigsawName);
	}
	
	static Feature vines(WeightedList<Block> blocks, Optional<HolderSet<Block>> canPlaceOn, IntProvider maxLength) {
		return new VinesFeature(blocks, canPlaceOn, maxLength);
	}
	
	static Feature well(BlockStateProvider groundProvider, BlockStateProvider suspiciousProvider, BlockStateProvider standardProvider, BlockStateProvider slabProvider, BlockStateProvider fluidProvider, IntProvider suspiciousPlacements, ResourceKey<LootTable> suspiciousLootTable) {
		return new WellFeature(groundProvider, suspiciousProvider, standardProvider, slabProvider, fluidProvider, suspiciousPlacements, suspiciousLootTable);
	}
}
