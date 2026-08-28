package dev.worldgen.lithostitched.impl.worldgen.biomeinjector;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record DispatchAlternateLayout(Optional<LoadPredicate> predicate, ResourceKey<LevelStem> dimension, int priority, ParameterMap parameters, ParameterList<Holder<Biome>> points) implements BiomeInjector {
	public static final MapCodec<DispatchAlternateLayout> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LoadPredicate.FIELD_CODEC.forGetter(DispatchAlternateLayout::predicate),
		BiomeInjector.DIMENSION_CODEC.forGetter(DispatchAlternateLayout::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(DispatchAlternateLayout::priority),
		ParameterMap.CODEC.forGetter(DispatchAlternateLayout::parameters),
		ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("points").forGetter(DispatchAlternateLayout::points)
	).apply(i, DispatchAlternateLayout::new));
	
	public boolean matches(int blockX, int blockY, int blockZ, DensitySamplerSet samplers, Climate.TargetPoint point, ResourceKey<Region> currentRegion) {
		return this.parameters.matches(blockX, blockY, blockZ, samplers, point, currentRegion);
	}
	
	@Override
	public List<Holder<Biome>> possibleBiomes() {
		return this.points.values().stream().map(Pair::getSecond).toList();
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
