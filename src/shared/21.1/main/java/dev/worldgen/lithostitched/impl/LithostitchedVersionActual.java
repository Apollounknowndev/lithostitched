package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.worldgen.poolelement.LithostitchedFeaturePoolElement;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.Map;

public class LithostitchedVersionActual {
	@Actual
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
	}
	
	@Actual
	public static void initVersionRegistrations() {
		LithostitchedRegistrar.register(Registries.STRUCTURE_POOL_ELEMENT, Map.ofEntries(
			Map.entry("feature", LithostitchedFeaturePoolElement.TYPE)
		));
	}
	
	@Actual
	public static Codec<FloatProvider> floatProviderCodec(float min, float max) {
		return FloatProvider.codec(min, max);
	}
	
	@Actual
	public static Codec<IntProvider> intProviderCodec(int min, int max) {
		return IntProvider.codec(min, max);
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
		return provider.getMinValue();
	}
	
	@Actual
	public static int maxInclusive(IntProvider provider) {
		return provider.getMaxValue();
	}
	
	@Actual
	public static long getSeed(MinecraftServer server) {
		return server.getWorldData().worldGenOptions().seed();
	}
	
	@Actual
	public static BlockState getState(BlockStateProvider provider, WorldGenLevel level, RandomSource random, BlockPos pos) {
		return provider.getState(random, pos);
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
}
