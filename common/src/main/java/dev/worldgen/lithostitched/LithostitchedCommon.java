package dev.worldgen.lithostitched;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.densityfunction.MergedDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.WrappedMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.feature.DungeonFeature;
import dev.worldgen.lithostitched.worldgen.feature.StructureTemplateFeature;
import dev.worldgen.lithostitched.worldgen.feature.WellFeature;
import dev.worldgen.lithostitched.worldgen.feature.config.DungeonFeatureConfig;
import dev.worldgen.lithostitched.worldgen.feature.config.StructureTemplateConfig;
import dev.worldgen.lithostitched.worldgen.feature.config.WellFeatureConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.poolalias.RandomEntries;
import dev.worldgen.lithostitched.worldgen.poolelement.GuaranteedPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.LimitedPoolElement;
import dev.worldgen.lithostitched.worldgen.processor.ApplyRandomStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.BlockSwapStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.ReferenceStructureProcessor;
import dev.worldgen.lithostitched.worldgen.ruletest.MatchingBlocksRuleTest;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import dev.worldgen.lithostitched.worldgen.structure.condition.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
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

	private LithostitchedCommon() {}

	public static void init() {}

	public static <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> resourceKey, String name) {
		return ResourceKey.create(resourceKey, id(name));
	}

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

	public static void registerCommonModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
		consumer.accept("add_processor_list_processors", AddProcessorListProcessorsModifier.CODEC);
		consumer.accept("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC);
		consumer.accept("add_surface_rule", AddSurfaceRuleModifier.CODEC);
		consumer.accept("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC);
		consumer.accept("no_op", NoOpModifier.CODEC);
		consumer.accept("redirect_feature", RedirectFeatureModifier.CODEC);
		consumer.accept("remove_structures_from_structure_set", RemoveStructuresFromStructureSetModifier.CODEC);
		consumer.accept("set_pool_aliases", SetPoolAliasesModifier.CODEC);
		consumer.accept("wrap_density_function", WrapDensityFunctionModifier.CODEC);
		consumer.accept("wrap_noise_router", WrapNoiseRouterModifier.CODEC);
	}

	public static void registerCommonFeatureTypes(BiConsumer<String, Feature<?>> consumer) {
		consumer.accept("dungeon", new DungeonFeature(DungeonFeatureConfig.CODEC));
		consumer.accept("structure_template", new StructureTemplateFeature(StructureTemplateConfig.CODEC));
		consumer.accept("well", new WellFeature(WellFeatureConfig.CODEC));
	}

	public static void registerCommonPoolElementTypes(BiConsumer<String, StructurePoolElementType<?>> consumer) {
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

	public static void registerCommonStructureConditions(BiConsumer<String, MapCodec<? extends StructureCondition>> consumer) {
		consumer.accept("any_of", AnyOfStructureCondition.CODEC);
		consumer.accept("all_of", AllOfStructureCondition.CODEC);
		consumer.accept("height_filter", HeightFilterStructureCondition.CODEC);
		consumer.accept("in_biome", InBiomeStructureCondition.CODEC);
		consumer.accept("not", NotStructureCondition.CODEC);
		consumer.accept("offset", OffsetStructureCondition.CODEC);
		consumer.accept("sample_density", SampleDensityStructureCondition.CODEC);
		consumer.accept("true", TrueStructureCondition.CODEC);
	}

	public static void registerCommonStructureProcessors(BiConsumer<String, StructureProcessorType<?>> consumer) {
		consumer.accept("apply_random", ApplyRandomStructureProcessor.TYPE);
		consumer.accept("block_swap", BlockSwapStructureProcessor.TYPE);
		consumer.accept("reference", ReferenceStructureProcessor.TYPE);
	}

	public static void registerCommonRuleTests(BiConsumer<String, RuleTestType<?>> consumer) {
		consumer.accept("matching_blocks", MatchingBlocksRuleTest.TYPE);
	}

	public static void registerCommonBlockEntityModifiers(BiConsumer<String, RuleBlockEntityModifierType<?>> consumer) {
		consumer.accept("apply_all", ApplyAll.TYPE);
		consumer.accept("apply_random", ApplyRandom.TYPE);
	}
}
