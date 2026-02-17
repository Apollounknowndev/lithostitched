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

public record ForcePlacement(ResourceKey<LevelStem> dimension, int priority, Holder<Biome> biome, ParameterMap parameters) implements BiomeInjector {
	public static final MapCodec<ForcePlacement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeInjector.DIMENSION_CODEC.forGetter(ForcePlacement::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(ForcePlacement::priority),
		Biome.CODEC.fieldOf("biome").forGetter(ForcePlacement::biome),
		ParameterMap.CODEC.forGetter(ForcePlacement::parameters)
	).apply(i, ForcePlacement::new));
	
	@Override
	public void mapAll(NoiseWiringHelper noiseHelper) {
		this.parameters.mapAll(noiseHelper);
	}
	
	public boolean matches(DensityFunction.FunctionContext context, Climate.TargetPoint point, HashMap<DensityFunction, Double> densities) {
		return this.parameters.matches(context, point, densities);
	}
	
	@Override
	public List<Holder<Biome>> biomes() {
		return List.of(this.biome);
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
