package dev.worldgen.lithostitched;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.blockpredicate.BlockStatePredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.MultipleOfPredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.RandomChancePredicate;
import dev.worldgen.lithostitched.worldgen.densityfunction.MergedDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.WrappedMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.feature.*;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.internal.CompileRawTemplatesModifier;
import dev.worldgen.lithostitched.worldgen.placementcondition.*;
import dev.worldgen.lithostitched.worldgen.placementmodifier.ConditionPlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.NoiseSlopePlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.OffsetPlacement;
import dev.worldgen.lithostitched.worldgen.poolalias.RandomEntries;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.legacy.GuaranteedPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.legacy.LimitedPoolElement;
import dev.worldgen.lithostitched.worldgen.processor.*;
import dev.worldgen.lithostitched.worldgen.processor.condition.*;
import dev.worldgen.lithostitched.worldgen.stateprovider.RandomBlockProvider;
import dev.worldgen.lithostitched.worldgen.stateprovider.WeightedProvider;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

/**
 * Class containing core fields and methods used commonly by Lithostitched across mod loaders.
 * <p>Undocumented methods can be considered not API.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedCommon {
	public static final String MOD_ID = "lithostitched";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> resourceKey, String name) {
		return ResourceKey.create(resourceKey, id(name));
	}

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

	public static void registerCommonModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
		consumer.accept("internal/compile_raw_templates", CompileRawTemplatesModifier.CODEC);
		consumer.accept("add_processor_list_processors", AddProcessorListProcessorsModifier.CODEC);
		consumer.accept("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC);
		consumer.accept("add_surface_rule", AddSurfaceRuleModifier.CODEC);
		consumer.accept("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC);
		consumer.accept("no_op", NoOpModifier.CODEC);
		consumer.accept("remove_structure_set_entries", RemoveStructureSetEntriesModifier.CODEC);
		consumer.accept("set_pool_aliases", SetPoolAliasesModifier.CODEC);
		consumer.accept("set_pool_element_processors", SetPoolElementProcessorsModifier.CODEC);
		consumer.accept("set_structure_spawn_condition", SetStructureSpawnConditionModifier.CODEC);
		consumer.accept("stack_feature", StackFeatureModifier.CODEC);
		consumer.accept("wrap_density_function", WrapDensityFunctionModifier.CODEC);
		consumer.accept("wrap_noise_router", WrapNoiseRouterModifier.CODEC);
	}

	public static void registerCommonBlockPredicateTypes(BiConsumer<String, BlockPredicateType<?>> consumer) {
		consumer.accept("block_state", BlockStatePredicate.TYPE);
		consumer.accept("multiple_of", MultipleOfPredicate.TYPE);
		consumer.accept("random_chance", RandomChancePredicate.TYPE);
	}

	public static void registerCommonStateProviders(BiConsumer<String, BlockStateProviderType<?>> consumer) {
		consumer.accept("weighted", WeightedProvider.TYPE);
		consumer.accept("random_block", RandomBlockProvider.TYPE);
	}

	public static void registerCommonPlacementModifiers(BiConsumer<String, PlacementModifierType<?>> consumer) {
		consumer.accept("condition", ConditionPlacement.TYPE);
		consumer.accept("noise_slope", NoiseSlopePlacement.TYPE);
		consumer.accept("offset", OffsetPlacement.TYPE);
	}

	public static void registerCommonFeatureTypes(BiConsumer<String, Feature<?>> consumer) {
		consumer.accept("composite", CompositeFeature.FEATURE);
		consumer.accept("dungeon", DungeonFeature.FEATURE);
		consumer.accept("large_dripstone", LargeDripstoneFeature.FEATURE);
		consumer.accept("ore", OreFeature.FEATURE);
		consumer.accept("select", SelectFeature.FEATURE);
		consumer.accept("structure_template", StructureTemplateFeature.FEATURE);
		consumer.accept("weighted_selector", WeightedSelectorFeature.FEATURE);
		consumer.accept("well", WellFeature.FEATURE);
		consumer.accept("vines", VinesFeature.FEATURE);
	}

	public static void registerCommonPoolElementTypes(BiConsumer<String, StructurePoolElementType<?>> consumer) {
		consumer.accept("delegating", DelegatingPoolElement.TYPE);
		consumer.accept("guaranteed", GuaranteedPoolElement.TYPE);
		consumer.accept("limited", LimitedPoolElement.TYPE);
	}

	public static void registerCommonDensityFunctions(BiConsumer<String, MapCodec<? extends DensityFunction>> consumer) {
		consumer.accept("internal/merged", MergedDensityFunction.CODEC.codec());
		consumer.accept("wrapped_marker", WrappedMarkerDensityFunction.CODEC.codec());
		consumer.accept("original_marker", OriginalMarkerDensityFunction.CODEC.codec());
	}

	public static void registerCommonPoolAliasBindings(BiConsumer<String, MapCodec<? extends PoolAliasBinding>> consumer) {
		consumer.accept("internal/random_entries", RandomEntries.CODEC);
	}

	public static void registerCommonStructureTypes(BiConsumer<String, StructureType<?>> consumer) {
		consumer.accept("delegating", DelegatingStructure.TYPE);
		consumer.accept("jigsaw", AlternateJigsawStructure.TYPE);
	}

	public static void registerCommonPlacementConditions(BiConsumer<String, MapCodec<? extends PlacementCondition>> consumer) {
		consumer.accept("any_of", AnyOfPlacementCondition.CODEC);
		consumer.accept("all_of", AllOfPlacementCondition.CODEC);
		consumer.accept("grid", GridPlacementCondition.CODEC);
		consumer.accept("height_filter", HeightFilterPlacementCondition.CODEC);
		consumer.accept("in_biome", InBiomePlacementCondition.CODEC);
		consumer.accept("multiple_of", MultipleOfPlacementCondition.CODEC);
		consumer.accept("not", NotPlacementCondition.CODEC);
		consumer.accept("offset", OffsetPlacementCondition.CODEC);
		consumer.accept("sample_density", SampleDensityPlacementCondition.CODEC);
		consumer.accept("true", TruePlacementCondition.CODEC);
	}

	public static void registerCommonStructureProcessors(BiConsumer<String, StructureProcessorType<?>> consumer) {
		consumer.accept("internal/unbound_reference", UnboundReferenceProcessor.TYPE);
		consumer.accept("apply_random", ApplyRandomStructureProcessor.TYPE);
		consumer.accept("block_swap", BlockSwapStructureProcessor.TYPE);
		consumer.accept("reference", ReferenceStructureProcessor.TYPE);

		consumer.accept("condition", ConditionProcessor.TYPE);
		consumer.accept("discard_input", DiscardInputProcessor.TYPE);
		consumer.accept("schedule_tick", ScheduleTickProcessor.TYPE);
		consumer.accept("set_block", SetBlockProcessor.TYPE);
	}

	public static void registerCommonProcessorConditions(BiConsumer<String, MapCodec<? extends ProcessorCondition>> consumer) {
		consumer.accept("all_of", AllOf.CODEC);
		consumer.accept("any_of", AnyOf.CODEC);
		consumer.accept("matching_blocks", MatchingBlocks.CODEC);
		consumer.accept("not", Not.CODEC);
		consumer.accept("position", Position.CODEC);
		consumer.accept("random_chance", RandomChance.CODEC);
		consumer.accept("true", True.CODEC);
	}

	public static void registerCommonBlockEntityModifiers(BiConsumer<String, RuleBlockEntityModifierType<?>> consumer) {
		consumer.accept("apply_all", ApplyAll.TYPE);
		consumer.accept("apply_random", ApplyRandom.TYPE);
	}

	public static void debug(String message, Object... arguments) {
		if (ConfigHandler.getConfig().logDebugMessages()) {
			LithostitchedCommon.LOGGER.warn(message, arguments);
		}
	}
}
