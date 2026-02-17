package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.List;

public record ReplaceFully(ResourceKey<LevelStem> dimension, HolderSet<Biome> targets, Holder<Biome> replacement) implements BiomeInjector {
	public static final MapCodec<ReplaceFully> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeInjector.DIMENSION_CODEC.forGetter(ReplaceFully::dimension),
		Biome.LIST_CODEC.fieldOf("targets").forGetter(ReplaceFully::targets),
		Biome.CODEC.fieldOf("replacement").forGetter(ReplaceFully::replacement)
	).apply(i, ReplaceFully::new));
	
	public static void apply(ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters, List<BiomeInjector> injectors) {
		List<Holder<Biome>> targetBiomes = new ArrayList<>();
		for (BiomeInjector injector : injectors) {
			ReplaceFully replaceFully = (ReplaceFully) injector;
			targetBiomes.addAll(replaceFully.targets.stream().toList());
		}
		
		ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> replacedParameters = new ArrayList<>();
		for (var pair : parameters) {
			if (!targetBiomes.contains(pair.getSecond())) {
				replacedParameters.add(pair);
				continue;
			}
			
			boolean replaced = false;
			for (BiomeInjector injector : injectors) {
				ReplaceFully replaceFully = (ReplaceFully) injector;
				
				if (replaceFully.targets.contains(pair.getSecond())) {
					replaced = true;
					replacedParameters.add(new Pair<>(pair.getFirst(), replaceFully.replacement));
					break;
				}
			}
			
			if (!replaced) {
				replacedParameters.add(pair);
			}
		}
		parameters.clear();
		parameters.addAll(replacedParameters);
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
