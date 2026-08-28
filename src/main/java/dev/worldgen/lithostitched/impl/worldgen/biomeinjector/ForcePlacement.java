package dev.worldgen.lithostitched.impl.worldgen.biomeinjector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterMap;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record ForcePlacement(Optional<LoadPredicate> predicate, ResourceKey<LevelStem> dimension, int priority, Holder<Biome> biome, ParameterMap parameters) implements BiomeInjector {
	public static final MapCodec<ForcePlacement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LoadPredicate.FIELD_CODEC.forGetter(ForcePlacement::predicate),
		BiomeInjector.DIMENSION_CODEC.forGetter(ForcePlacement::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(ForcePlacement::priority),
		Biome.CODEC.fieldOf("biome").forGetter(ForcePlacement::biome),
		ParameterMap.CODEC.forGetter(ForcePlacement::parameters)
	).apply(i, ForcePlacement::new));
	
	public boolean matches(int blockX, int blockY, int blockZ, DensitySamplerSet samplers, Climate.TargetPoint point, ResourceKey<Region> currentRegion) {
		return this.parameters.matches(blockX, blockY, blockZ, samplers, point, currentRegion);
	}
	
	@Override
	public List<Holder<Biome>> possibleBiomes() {
		return List.of(this.biome);
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
