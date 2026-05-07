package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import dev.worldgen.lithostitched.impl.predicate.*;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.modifier.*;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.BaseBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.RepeatingBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.WrappedBand;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.blockpredicate.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.FastNoiseDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.CellularNoiseType;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.PerlinNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.fastnoise.config.SimplexNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.WrappedMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.feature.*;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.internal.CompileRawTemplatesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
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
import dev.worldgen.lithostitched.worldgen.surface.condition.AllOfCondition;
import dev.worldgen.lithostitched.worldgen.surface.condition.AnyOfCondition;
import dev.worldgen.lithostitched.worldgen.surface.condition.BiomeCondition;
import dev.worldgen.lithostitched.worldgen.surface.condition.SlopeCondition;
import dev.worldgen.lithostitched.worldgen.surface.condition.internal.TagFilledCondition;
import dev.worldgen.lithostitched.worldgen.surface.rule.BandlandsRule;
import dev.worldgen.lithostitched.worldgen.surface.rule.ReferenceRule;
import dev.worldgen.lithostitched.worldgen.surface.rule.TransientMergedRule;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Map;

/**
 * All of Lithostitched's static registries.
 */
@SuppressWarnings("unused")
public class LithostitchedBuiltInRegistries {
	public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = create(LithostitchedRegistries.MODIFIER_TYPE);
	public static final Registry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = create(LithostitchedRegistries.PLACEMENT_CONDITION_TYPE);
	public static final Registry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = create(LithostitchedRegistries.PROCESSOR_CONDITION_TYPE);
	public static final Registry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = create(LithostitchedRegistries.BANDLANDS_BAND_TYPE);
	public static final Registry<MapCodec<? extends BiomeInjector>> BIOME_INJECTOR_TYPE = create(LithostitchedRegistries.BIOME_INJECTOR_TYPE);
	public static final Registry<MapCodec<? extends FastNoiseConfig>> FAST_NOISE_CONFIG_TYPE = create(LithostitchedRegistries.FAST_NOISE_CONFIG_TYPE);
	public static final Registry<MapCodec<? extends LoadPredicate>> LOAD_PREDICATE_TYPE = create(LithostitchedRegistries.LOAD_PREDICATE_TYPE);
	
	@Expect
	private static <T> Registry<T> create(ResourceKey<Registry<T>> key);
	
	/**
	 * Purely for use in Lithostitched, don't call this.
	 */
	public static void init() {
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.WORLDGEN_MODIFIER, WorldgenModifier.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.SURFACE_RULE, SurfaceRules.RuleSource.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.BANDLANDS, Bandlands.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.TEMPLATE_LIST, TemplateList.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.BIOME_INJECTOR, BiomeInjector.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC);
		LithostitchedRegistrar.registerRegistry(LithostitchedRegistries.REGION, Region.CODEC);
		
		LithostitchedPlatform.initPlatformRegistrations();
		LithostitchedVersion.initVersionRegistrations();
		
		LithostitchedRegistrar.register(LithostitchedRegistries.MODIFIER_TYPE, Map.ofEntries(
			Map.entry("internal/compile_raw_templates", CompileRawTemplatesModifier.CODEC),
			Map.entry("add_biome_spawns", AddBiomeSpawnsModifier.CODEC),
			Map.entry("add_features", AddFeaturesModifier.CODEC),
			Map.entry("add_processor_list_processors", AddProcessorListProcessorsModifier.CODEC),
			Map.entry("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC),
			Map.entry("add_structure_templates", AddStructureTemplatesModifier.CODEC),
			Map.entry("add_surface_rule", AddSurfaceRuleModifier.CODEC),
			Map.entry("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC),
			Map.entry("no_op", NoOpModifier.CODEC),
			Map.entry("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC),
			Map.entry("remove_features", RemoveFeaturesModifier.CODEC),
			Map.entry("remove_structure_set_entries", RemoveStructureSetEntriesModifier.CODEC),
			Map.entry("replace_climate", ReplaceClimateModifier.CODEC),
			Map.entry("replace_effects", ReplaceEffectsModifier.CODEC),
			Map.entry("set_pool_aliases", SetPoolAliasesModifier.CODEC),
			Map.entry("set_pool_element_processors", SetPoolElementProcessorsModifier.CODEC),
			Map.entry("set_structure_spawn_condition", SetStructureSpawnConditionModifier.CODEC),
			Map.entry("stack_feature", StackFeatureModifier.CODEC),
			Map.entry("wrap_density_function", WrapDensityFunctionModifier.CODEC),
			Map.entry("wrap_noise_router", WrapNoiseRouterModifier.CODEC)
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.LOAD_PREDICATE_TYPE, Map.ofEntries(
			Map.entry("all_of", AllOfPredicate.CODEC),
			Map.entry("any_of", AnyOfPredicate.CODEC),
			Map.entry("loader", LoaderPredicate.CODEC),
			Map.entry("mod_loaded", ModLoadedPredicate.CODEC),
			Map.entry("not", NotPredicate.CODEC),
			Map.entry("pack_format", PackFormatPredicate.CODEC),
			Map.entry("true", TruePredicate.CODEC)
		));
		LithostitchedRegistrar.register(Registries.BIOME_SOURCE, Map.ofEntries(
			Map.entry("injector", InjectorBiomeSource.CODEC)
		));
		LithostitchedRegistrar.register(Registries.BLOCK_PREDICATE_TYPE, Map.ofEntries(
			Map.entry("block_state", BlockStatePredicate.TYPE),
			Map.entry("grid", GridPredicate.TYPE),
			Map.entry("in_structure", InStructurePredicate.TYPE),
			Map.entry("matching_biomes", MatchingBiomesPredicate.TYPE),
			Map.entry("multiple_of", MultipleOfPredicate.TYPE),
			Map.entry("offset", OffsetPredicate.TYPE),
			Map.entry("random_chance", RandomChancePredicate.TYPE)
		));
		LithostitchedRegistrar.register(Registries.BLOCK_STATE_PROVIDER_TYPE, Map.ofEntries(
			Map.entry("weighted", WeightedProvider.TYPE),
			Map.entry("random_block", RandomBlockProvider.TYPE)
		));
		LithostitchedRegistrar.register(Registries.PLACEMENT_MODIFIER_TYPE, Map.ofEntries(
			Map.entry("condition", ConditionPlacement.TYPE),
			Map.entry("noise_slope", NoiseSlopePlacement.TYPE),
			Map.entry("offset", OffsetPlacement.TYPE)
		));
		LithostitchedRegistrar.register(Registries.FEATURE, Map.ofEntries(
			Map.entry("composite", CompositeFeature.FEATURE),
			Map.entry("dungeon", DungeonFeature.FEATURE),
			Map.entry("large_dripstone", LargeDripstoneFeature.FEATURE),
			Map.entry("ore", OreFeature.FEATURE),
			Map.entry("placed", SimplePlacedFeature.FEATURE),
			Map.entry("select", SelectFeature.FEATURE),
			Map.entry("structure_template", StructureTemplateFeature.FEATURE),
			Map.entry("weighted_selector", WeightedSelectorFeature.FEATURE),
			Map.entry("well", WellFeature.FEATURE),
			Map.entry("vines", VinesFeature.FEATURE)
		));
		LithostitchedRegistrar.register(Registries.STRUCTURE_POOL_ELEMENT, Map.ofEntries(
			Map.entry("delegating", DelegatingPoolElement.TYPE),
			Map.entry("guaranteed", GuaranteedPoolElement.TYPE),
			Map.entry("limited", LimitedPoolElement.TYPE)
		));
		LithostitchedRegistrar.register(Registries.DENSITY_FUNCTION_TYPE, Map.ofEntries(
			Map.entry("internal/merged", MergedDensityFunction.CODEC.codec()),
			Map.entry("wrapped_marker", WrappedMarkerDensityFunction.CODEC.codec()),
			Map.entry("original_marker", OriginalMarkerDensityFunction.CODEC.codec()),
			Map.entry("fast_noise", FastNoiseDensityFunction.CODEC.codec()),
			
			Map.entry("axis", AxisDensityFunction.DATA_CODEC),
			Map.entry("ceil", CeilDensityFunction.DATA_CODEC),
			Map.entry("cos", CosDensityFunction.DATA_CODEC),
			Map.entry("floor", FloorDensityFunction.DATA_CODEC),
			Map.entry("mix", MixDensityFunction.DATA_CODEC),
			Map.entry("select", SelectDensityFunction.DATA_CODEC),
			Map.entry("shift", ShiftDensityFunction.DATA_CODEC),
			Map.entry("sin", SinDensityFunction.DATA_CODEC),
			Map.entry("sqrt", SqrtDensityFunction.DATA_CODEC)
		));
		LithostitchedRegistrar.register(Registries.POOL_ALIAS_BINDING, Map.ofEntries(
			Map.entry("internal/random_entries", RandomEntries.CODEC)
		));
		LithostitchedRegistrar.register(Registries.STRUCTURE_TYPE, Map.ofEntries(
			Map.entry("delegating", DelegatingStructure.TYPE),
			Map.entry("jigsaw", AlternateJigsawStructure.TYPE)
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.PLACEMENT_CONDITION_TYPE, Map.ofEntries(
			Map.entry("any_of", AnyOfPlacementCondition.CODEC),
			Map.entry("all_of", AllOfPlacementCondition.CODEC),
			Map.entry("grid", GridPlacementCondition.CODEC),
			Map.entry("height_filter", HeightFilterPlacementCondition.CODEC),
			Map.entry("in_biome", InBiomePlacementCondition.CODEC),
			Map.entry("multiple_of", MultipleOfPlacementCondition.CODEC),
			Map.entry("not", NotPlacementCondition.CODEC),
			Map.entry("offset", OffsetPlacementCondition.CODEC),
			Map.entry("sample_density", SampleDensityPlacementCondition.CODEC),
			Map.entry("sample_noise_router", SampleNoiseRouterPlacementCondition.CODEC),
			Map.entry("true", TruePlacementCondition.CODEC)
		));
		LithostitchedRegistrar.register(Registries.STRUCTURE_PROCESSOR, Map.ofEntries(
			Map.entry("internal/unbound_reference", UnboundReferenceProcessor.TYPE),
			Map.entry("apply_random", ApplyRandomStructureProcessor.TYPE),
			Map.entry("block_swap", BlockSwapStructureProcessor.TYPE),
			Map.entry("reference", ReferenceStructureProcessor.TYPE),
			
			Map.entry("condition", ConditionProcessor.TYPE),
			Map.entry("discard_input", DiscardInputProcessor.TYPE),
			Map.entry("schedule_tick", ScheduleTickProcessor.TYPE),
			Map.entry("set_block", SetBlockProcessor.TYPE)
		));
			
		LithostitchedRegistrar.register(LithostitchedRegistries.PROCESSOR_CONDITION_TYPE, Map.ofEntries(
			Map.entry("all_of", AllOf.CODEC),
			Map.entry("any_of", AnyOf.CODEC),
			Map.entry("matching_blocks", MatchingBlocks.CODEC),
			Map.entry("not", Not.CODEC),
			Map.entry("position", Position.CODEC),
			Map.entry("random_chance", RandomChance.CODEC),
			Map.entry("true", True.CODEC)
		));
		LithostitchedRegistrar.register(Registries.RULE_BLOCK_ENTITY_MODIFIER, Map.ofEntries(
			Map.entry("apply_all", ApplyAll.TYPE),
			Map.entry("apply_random", ApplyRandom.TYPE)
		));
		LithostitchedRegistrar.register(Registries.MATERIAL_RULE, Map.ofEntries(
			Map.entry("transient_merged", TransientMergedRule.CODEC.codec()),
			Map.entry("bandlands", BandlandsRule.CODEC.codec()),
			Map.entry("reference", ReferenceRule.CODEC.codec())
		));
		LithostitchedRegistrar.register(Registries.MATERIAL_CONDITION, Map.ofEntries(
			Map.entry("internal/tag_filled", TagFilledCondition.CODEC.codec()),
			
			Map.entry("all_of", AllOfCondition.CODEC.codec()),
			Map.entry("any_of", AnyOfCondition.CODEC.codec()),
			Map.entry("biome", BiomeCondition.CODEC.codec()),
			Map.entry("slope", SlopeCondition.CODEC.codec())
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.BANDLANDS_BAND_TYPE, Map.ofEntries(
			Map.entry("base", BaseBand.CODEC),
			Map.entry("repeating", RepeatingBand.CODEC),
			Map.entry("wrapped", WrappedBand.CODEC)
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.FAST_NOISE_CONFIG_TYPE, Map.ofEntries(
			Map.entry("cellular", CellularNoiseType.CODEC),
			Map.entry("perlin", PerlinNoiseType.CODEC),
			Map.entry("simplex", SimplexNoiseType.CODEC)
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.BIOME_INJECTOR_TYPE, Map.ofEntries(
			Map.entry("add_points", AddPoints.CODEC),
			Map.entry("dispatch_alternate_layout", DispatchAlternateLayout.CODEC),
			Map.entry("force_placement", ForcePlacement.CODEC),
			Map.entry("replace_fully", ReplaceFully.CODEC),
			Map.entry("replace_partially", ReplacePartially.CODEC)
		));
	}
}
