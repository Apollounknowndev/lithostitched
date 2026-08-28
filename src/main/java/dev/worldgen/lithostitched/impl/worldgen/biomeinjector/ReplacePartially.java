package dev.worldgen.lithostitched.impl.worldgen.biomeinjector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterMap;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record ReplacePartially(Optional<LoadPredicate> predicate, ResourceKey<LevelStem> dimension, int priority, HolderSet<Biome> targets, Holder<Biome> replacement, ParameterMap parameters) implements BiomeInjector {
	public static final MapCodec<ReplacePartially> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LoadPredicate.FIELD_CODEC.forGetter(ReplacePartially::predicate),
		BiomeInjector.DIMENSION_CODEC.forGetter(ReplacePartially::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(ReplacePartially::priority),
		Biome.LIST_CODEC.fieldOf("targets").forGetter(ReplacePartially::targets),
		Biome.CODEC.fieldOf("replacement").forGetter(ReplacePartially::replacement),
		ParameterMap.CODEC.forGetter(ReplacePartially::parameters)
	).apply(i, ReplacePartially::new));

	public boolean matches(int blockX, int blockY, int blockZ, DensitySamplerSet samplers, Climate.TargetPoint point, Holder<Biome> biome, ResourceKey<Region> currentRegion) {
		if (!this.targets().contains(biome)) return false;
		return this.parameters.matches(blockX, blockY, blockZ, samplers, point, currentRegion);
	}
	
	@Override
	public List<Holder<Biome>> possibleBiomes() {
		return List.of(this.replacement);
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
