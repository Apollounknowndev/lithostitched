package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.*;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.*;
import dev.worldgen.lithostitched.impl.worldgen.processor.*;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.*;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.internal.*;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.*;
import dev.worldgen.lithostitched.worldgen.attribute.LithostitchedEnvironmentAttributes;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetBiomeAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetDimensionAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetTimelineTracksModifier;
import dev.worldgen.lithostitched.worldgen.poolelement.LithostitchedFeaturePoolElement;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.msrandom.multiplatform.annotations.Actual;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.List;
import java.util.Map;

public class LithostitchedVersionActual {
	@Actual
	public static Codec<DensityFunction> DF_CODEC = DensityFunction.CODEC;
	
	@Actual
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).major();
	}
	
	@Actual
	public static void initVersionRegistrations() {
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
		LithostitchedRegistrar.register(Registries.MATERIAL_RULE, Map.ofEntries(
			Map.entry("transient_merged", TransientMergedRule.CODEC),
			Map.entry("bandlands", BandlandsRule.CODEC),
			Map.entry("reference", ReferenceRule.CODEC)
		));
		LithostitchedRegistrar.register(Registries.MATERIAL_CONDITION, Map.ofEntries(
			Map.entry("internal/tag_filled", TagFilledCondition.CODEC),
			
			Map.entry("all_of", AllOfCondition.CODEC),
			Map.entry("any_of", AnyOfCondition.CODEC),
			Map.entry("biome", BiomeCondition.CODEC),
			Map.entry("slope", SlopeCondition.CODEC)
		));
		LithostitchedRegistrar.register(Registries.STRUCTURE_PROCESSOR, Map.ofEntries(
			Map.entry("internal/unbound_reference", UnboundReferenceProcessor.CODEC),
			Map.entry("apply_random", ApplyRandomStructureProcessor.CODEC),
			Map.entry("block_swap", BlockSwapStructureProcessor.CODEC),
			Map.entry("reference", ReferenceStructureProcessor.CODEC),
			
			Map.entry("condition", ConditionProcessor.CODEC),
			Map.entry("discard_input", DiscardInputProcessor.CODEC),
			Map.entry("schedule_tick", ScheduleTickProcessor.CODEC),
			Map.entry("set_block", SetBlockProcessor.CODEC)
		));
		LithostitchedRegistrar.register(LithostitchedRegistries.MODIFIER_TYPE, Map.ofEntries(
			Map.entry("set_biome_attributes", SetBiomeAttributesModifier.CODEC),
			Map.entry("set_dimension_attributes", SetDimensionAttributesModifier.CODEC),
			Map.entry("set_timeline_tracks", SetTimelineTracksModifier.CODEC)
		));
		LithostitchedRegistrar.register(Registries.ENVIRONMENT_ATTRIBUTE, Map.ofEntries(
			Map.entry("structure/reset_music", LithostitchedEnvironmentAttributes.RESET_MUSIC)
		));
		LithostitchedRegistrar.register(Registries.STRUCTURE_POOL_ELEMENT, Map.ofEntries(
			Map.entry("feature", LithostitchedFeaturePoolElement.TYPE)
		));
	}
	
	@Actual
	public static Codec<FloatProvider> floatProviderCodec(float min, float max) {
		return FloatProviders.CODEC;
	}
	
	@Actual
	public static Codec<IntProvider> intProviderCodec(int min, int max) {
		return IntProviders.CODEC;
	}
	
	@Actual
	public static IntProvider uniformInt(int min, int max) {
		return UniformInt.of(min, max);
	}
	
	@Actual
	public static IntProvider constantInt(int value) {
		return ConstantInt.of(value);
	}
	
	@Actual
	public static int minInclusive(IntProvider provider) {
		return provider.minInclusive();
	}
	
	@Actual
	public static int maxInclusive(IntProvider provider) {
		return provider.maxInclusive();
	}
	
	@Actual
	public static long getSeed(MinecraftServer server) {
		return server.getWorldGenSettings().options().seed();
	}
	
	@Actual
	public static BlockState getState(BlockStateProvider provider, WorldGenLevel level, RandomSource random, BlockPos pos) {
		return provider.getState(level, random, pos);
	}
	
	@Actual
	public static boolean stateIs(BlockState state, HolderSet<Block> blocks) {
		return state.is(blocks);
	}
	
	@Actual
	public static boolean stateIs(BlockState state, TagKey<Block> blocks) {
		return state.is(blocks);
	}
	
	@Actual
	public static boolean stateIs(BlockState state, Block block) {
		return state.is(block);
	}
	
	@Actual
	public static StructureProcessor getUnboundReferenceProcessor(String name) {
		return UnboundReferenceProcessor.of(name);
	}
	
	@Actual
	public static SurfaceRules.RuleSource handleRuleMerging(SurfaceRules.RuleSource original, List<SurfaceRules.RuleSource> additions) {
		if (original instanceof TransientMergedRule transientMerged) {
			transientMerged.rules().addAll(additions);
			return original;
		} else {
			return new TransientMergedRule(additions, original);
		}
	}
	
	@Actual
	public static <V extends T, T> V getStatic(Registry<T> registry, String name) {
		return (V) registry.getValue(Identifier.withDefaultNamespace(name));
	}
	
	
	@Actual
	public static StructureTemplate.StructureBlockInfo applyProcessor(StructureProcessor processor, WorldGenLevel level, BlockPos pos, BlockPos pivot, BlockPos templateRelative, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
		return processor.processBlock(level, pos, pivot, templateRelative, info, settings);
	}
	
}
