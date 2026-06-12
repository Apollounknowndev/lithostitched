package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.msrandom.multiplatform.annotations.Actual;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.List;

public class LithostitchedVersion {
	@Expect
	public static Codec<DensityFunction> DF_CODEC;
	
	@Expect
	public static int getPackFormat();
	
	@Expect
	public static void initVersionRegistrations();
	
	@Expect
	public static Codec<FloatProvider> floatProviderCodec(float min, float max);
	
	@Expect
	public static Codec<IntProvider> intProviderCodec(int min, int max);
	
	@Expect
	public static IntProvider uniformInt(int min, int max);
	
	@Expect
	public static IntProvider constantInt(int value);
	
	@Expect
	public static int minInclusive(IntProvider provider);
	
	@Expect
	public static int maxInclusive(IntProvider provider);
	
	@Expect
	public static long getSeed(MinecraftServer server);
	
	@Expect
	public static BlockState getState(BlockStateProvider provider, WorldGenLevel level, RandomSource random, BlockPos pos);
	
	@Expect
	public static boolean stateIs(BlockState state, HolderSet<Block> blocks);
	
	@Expect
	public static boolean stateIs(BlockState state, TagKey<Block> blocks);
	
	@Expect
	public static boolean stateIs(BlockState state, Block block);
	
	@Expect
	public static StructureProcessor getUnboundReferenceProcessor(String name);
	
	@Expect
	public static RuleSource handleRuleMerging(RuleSource original, List<RuleSource> additions);
	
	@Expect
	public static <V extends T, T> V getStatic(Registry<T> registry, String name);
	
	@Expect
	public static StructureTemplate.StructureBlockInfo applyProcessor(StructureProcessor processor, WorldGenLevel level, BlockPos pos, BlockPos pivot, BlockPos templateRelative, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings);
}
