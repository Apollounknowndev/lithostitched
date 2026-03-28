package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.worldgen.attribute.LithostitchedEnvironmentAttributes;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetBiomeAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetDimensionAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetTimelineTracksModifier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.msrandom.multiplatform.annotations.Actual;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Map;

public class LithostitchedVersionActual {
	@Actual
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).major();
	}
	
	@Actual
	public static void initVersionRegistrations() {
		LithostitchedRegistrar.register(LithostitchedRegistries.MODIFIER_TYPE, Map.ofEntries(
			Map.entry("set_biome_attributes", SetBiomeAttributesModifier.CODEC),
			Map.entry("set_dimension_attributes", SetDimensionAttributesModifier.CODEC),
			Map.entry("set_timeline_tracks", SetTimelineTracksModifier.CODEC)
		));
		LithostitchedRegistrar.register(Registries.ENVIRONMENT_ATTRIBUTE, Map.ofEntries(
			Map.entry("structure/reset_music", LithostitchedEnvironmentAttributes.RESET_MUSIC)
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
}
