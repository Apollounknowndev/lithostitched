package dev.worldgen.lithostitched;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.feature.DungeonFeature;
import dev.worldgen.lithostitched.worldgen.feature.WellFeature;
import dev.worldgen.lithostitched.worldgen.feature.config.DungeonFeatureConfig;
import dev.worldgen.lithostitched.worldgen.feature.config.WellFeatureConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.poolalias.ApplyWithChance;
import dev.worldgen.lithostitched.worldgen.poolalias.RandomEntries;
import dev.worldgen.lithostitched.worldgen.poolelement.GuaranteedPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.LimitedPoolElement;
import dev.worldgen.lithostitched.worldgen.processor.ApplyRandomStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.BlockSwapStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.ReferenceStructureProcessor;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import dev.worldgen.lithostitched.worldgen.structure.condition.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
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
		return ResourceKey.create(resourceKey, ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
	}

	public static void registerCommonModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
		consumer.accept("add_pool_aliases", AddPoolAliasesModifier.CODEC);
		consumer.accept("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC);
		consumer.accept("add_surface_rule", AddSurfaceRuleModifier.CODEC);
		consumer.accept("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC);
		consumer.accept("no_op", NoOpModifier.CODEC);
		consumer.accept("redirect_feature", RedirectFeatureModifier.CODEC);
		consumer.accept("remove_structures_from_structure_set", RemoveStructuresFromStructureSetModifier.CODEC);
	}

	public static void registerCommonFeatureTypes(BiConsumer<String, Feature<?>> consumer) {
		consumer.accept("dungeon", new DungeonFeature(DungeonFeatureConfig.CODEC));
		consumer.accept("well", new WellFeature(WellFeatureConfig.CODEC));
	}

	public static void registerCommonPoolElementTypes(BiConsumer<String, MapCodec<? extends StructurePoolElement>> consumer) {
		consumer.accept("guaranteed", GuaranteedPoolElement.CODEC);
		consumer.accept("limited", LimitedPoolElement.CODEC);
	}

	public static void registerCommonPoolAliasBindings(BiConsumer<String, MapCodec<? extends PoolAliasBinding>> consumer) {
		consumer.accept("apply_with_chance", ApplyWithChance.CODEC);
		consumer.accept("internal/random_entries", RandomEntries.CODEC);
	}


	public static void registerCommonStructureTypes(BiConsumer<String, MapCodec<? extends Structure>> consumer) {
		consumer.accept("delegating", DelegatingStructure.CODEC);
		consumer.accept("jigsaw", AlternateJigsawStructure.CODEC);
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

	public static void registerCommonStructureProcessors(BiConsumer<String, MapCodec<? extends StructureProcessor>> consumer) {
		consumer.accept("apply_random", ApplyRandomStructureProcessor.CODEC);
		consumer.accept("block_swap", BlockSwapStructureProcessor.CODEC);
		consumer.accept("reference", ReferenceStructureProcessor.CODEC);
	}

	public static void registerCommonBlockEntityModifiers(BiConsumer<String, MapCodec<? extends RuleBlockEntityModifier>> consumer) {
		consumer.accept("apply_all", ApplyAll.CODEC);
		consumer.accept("apply_random", ApplyRandom.CODEC);
	}
}
