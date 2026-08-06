package dev.worldgen.lithostitched.platform;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.worldgen.processor.*;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.*;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public class LithostitchedVersion {
	public static Codec<DensityFunction> DF_CODEC = DensityFunction.CODEC;
	
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).major();
	}
	
	public static Codec<FloatProvider> floatProviderCodec(float min, float max) {
		return FloatProviders.CODEC;
	}
	
	public static Codec<IntProvider> intProviderCodec(int min, int max) {
		return IntProviders.CODEC;
	}
	
	public static IntProvider uniformInt(int min, int max) {
		return UniformInt.of(min, max);
	}
	
	public static IntProvider constantInt(int value) {
		return ConstantInt.of(value);
	}
	
	public static int minInclusive(IntProvider provider) {
		return provider.minInclusive();
	}
	
	public static int maxInclusive(IntProvider provider) {
		return provider.maxInclusive();
	}
	
	public static BlockState getState(BlockStateProvider provider, WorldGenLevel level, RandomSource random, BlockPos pos) {
		return provider.getState(level, random, pos);
	}
	
	public static <V extends T, T> V getStatic(Registry<T> registry, String name) {
		return (V) registry.getValue(Identifier.withDefaultNamespace(name));
	}
	
	public static StructureTemplate.StructureBlockInfo applyProcessor(StructureProcessor processor, WorldGenLevel level, BlockPos pos, BlockPos pivot, BlockPos templateRelative, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
		return processor.processBlock(level, pos, pivot, templateRelative, info, settings);
	}
	
	public static String getString(CompoundTag tag, String name) {
		return tag.getStringOr(name, "");
	}
	
	public static DensityFunction getInitialDensity(NoiseRouter router) {
		return router.preliminarySurfaceLevel();
	}
	
	public static String getInitialDensityName() {
		return "preliminary_surface_level";
	}
}
