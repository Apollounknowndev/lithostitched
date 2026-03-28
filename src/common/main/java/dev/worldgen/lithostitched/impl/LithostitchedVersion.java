package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.msrandom.multiplatform.annotations.Expect;

public class LithostitchedVersion {
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
}
