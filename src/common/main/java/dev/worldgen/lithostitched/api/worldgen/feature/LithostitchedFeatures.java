package dev.worldgen.lithostitched.api.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.api.util.WeightedList;
import dev.worldgen.lithostitched.worldgen.feature.*;
import dev.worldgen.lithostitched.worldgen.feature.config.*;
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
	Feature<CompositeConfig> COMPOSITE = CompositeFeature.FEATURE;
	Feature<DungeonConfig> DUNGEON = DungeonFeature.FEATURE;
	Feature<LargeDripstoneConfig> LARGE_DRIPSTONE = LargeDripstoneFeature.FEATURE;
	Feature<OreConfig> ORE = OreFeature.FEATURE;
	Feature<SelectConfig> SELECT = SelectFeature.FEATURE;
	Feature<StructureTemplateConfig> STRUCTURE_TEMPLATE = StructureTemplateFeature.FEATURE;
	Feature<VinesConfig> VINES = VinesFeature.FEATURE;
	Feature<WeightedSelectorConfig> WEIGHTED_SELECTOR = WeightedSelectorFeature.FEATURE;

	static CompositeConfig placeAll(HolderSet<PlacedFeature> features) {
		return new CompositeConfig(features, CompositeConfig.Type.NEVER_CANCEL);
	}
	
	static CompositeConfig placeUntilFailure(HolderSet<PlacedFeature> features) {
		return new CompositeConfig(features, CompositeConfig.Type.CANCEL_ON_FAILURE);
	}
	
	static CompositeConfig placeUntilSuccess(HolderSet<PlacedFeature> features) {
		return new CompositeConfig(features, CompositeConfig.Type.CANCEL_ON_SUCCESS);
	}
	
	static DungeonConfig dungeon(int minOpenings, int maxOpenings, IntProvider radius, int maxChests, WeightedList<EntityType<?>> spawnerMobs, BlockStateProvider floorProvider, BlockStateProvider wallProvider, Optional<HolderSet<Block>> dungeonInvalidBlocks, ResourceKey<LootTable> lootTable) {
		return new DungeonConfig(minOpenings, maxOpenings, radius, maxChests, spawnerMobs, floorProvider, wallProvider, dungeonInvalidBlocks, lootTable);
	}
	
	static LargeDripstoneConfig largeDripstone(BlockStateProvider stateProvider, HolderSet<Block> replaceableBlocks, int floorToCeilingSearchRange, IntProvider columnRadius, FloatProvider heightScale, float maxColumnRadiusToCaveHeightRatio, FloatProvider stalactiteBluntness, FloatProvider stalagmiteBluntness, FloatProvider windSpeed, int minRadiusForWind, float minBluntnessForWind) {
		return new LargeDripstoneConfig(stateProvider, replaceableBlocks, floorToCeilingSearchRange, columnRadius, heightScale, maxColumnRadiusToCaveHeightRatio, stalactiteBluntness, stalagmiteBluntness, windSpeed, minRadiusForWind, minBluntnessForWind);
	}
	
	static OreConfig ore(int size, List<Pair<BlockPredicate, BlockStateProvider>> targets) {
		return OreConfig.create(size, targets);
	}
	
	static SelectConfig select(List<Pair<BlockPredicate, Holder<PlacedFeature>>> features) {
		return new SelectConfig(features);
	}
	
	static StructureTemplateConfig structureTemplate(Identifier template, Holder<StructureProcessorList> processors, LiquidSettings liquidSettings) {
		return new StructureTemplateConfig(template, processors, Optional.empty(), liquidSettings, Optional.empty());
	}
	
	static StructureTemplateConfig structureTemplate(Identifier template, Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings, Optional<Identifier> startJigsawName) {
		return new StructureTemplateConfig(template, processors, rotation, liquidSettings, startJigsawName);
	}
	
	static VinesConfig vines(WeightedList<Block> blocks, Optional<HolderSet<Block>> canPlaceOn, IntProvider maxLength) {
		return new VinesConfig(blocks, canPlaceOn, maxLength);
	}
	
	static WeightedSelectorConfig weightedSelector(WeightedList<Holder<PlacedFeature>> features) {
		return new WeightedSelectorConfig(features);
	}
	
	static WellConfig well(BlockStateProvider groundProvider, BlockStateProvider suspiciousProvider, BlockStateProvider standardProvider, BlockStateProvider slabProvider, BlockStateProvider fluidProvider, IntProvider suspiciousPlacements, ResourceKey<LootTable> suspiciousLootTable) {
		return new WellConfig(groundProvider, suspiciousProvider, standardProvider, slabProvider, fluidProvider, suspiciousPlacements, suspiciousLootTable);
	}
}
