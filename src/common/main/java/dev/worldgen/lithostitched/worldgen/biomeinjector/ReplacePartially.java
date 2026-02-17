package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import dev.worldgen.lithostitched.worldgen.biomeinjector.internal.ParameterMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.HashMap;
import java.util.List;

public record ReplacePartially(ResourceKey<LevelStem> dimension, HolderSet<Biome> targets, Holder<Biome> replacement, ParameterMap parameters) implements BiomeInjector {
	public static final MapCodec<ReplacePartially> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeInjector.DIMENSION_CODEC.forGetter(ReplacePartially::dimension),
		Biome.LIST_CODEC.fieldOf("targets").forGetter(ReplacePartially::targets),
		Biome.CODEC.fieldOf("replacement").forGetter(ReplacePartially::replacement),
		ParameterMap.CODEC.forGetter(ReplacePartially::parameters)
	).apply(i, ReplacePartially::new));
	
	@Override
	public void mapAll(NoiseWiringHelper noiseHelper) {
		this.parameters.mapAll(noiseHelper);
	}
	
	public boolean matches(DensityFunction.FunctionContext context, Climate.TargetPoint point, HashMap<DensityFunction, Double> densities, Holder<Biome> biome) {
		if (!this.targets().contains(biome)) return false;
		return this.parameters.matches(context, point, densities);
	}
	
	@Override
	public List<Holder<Biome>> biomes() {
		return List.of(this.replacement);
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
