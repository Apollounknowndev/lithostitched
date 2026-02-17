package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.List;

public record AddPoints(ResourceKey<LevelStem> dimension, int priority, ParameterList<Holder<Biome>> points) implements BiomeInjector {
	public static final MapCodec<AddPoints> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeInjector.DIMENSION_CODEC.forGetter(AddPoints::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(AddPoints::priority),
		ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("points").forGetter(AddPoints::points)
	).apply(i, AddPoints::new));
	
	public static void apply(ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters, List<BiomeInjector> injectors) {
		for (var injector : injectors) {
			parameters.addAll(((AddPoints)injector).points().values());
		}
	}
	
	@Override
	public List<Holder<Biome>> biomes() {
		return this.points.values().stream().map(Pair::getSecond).toList();
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}