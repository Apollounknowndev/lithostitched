package dev.worldgen.lithostitched.impl.worldgen.biomeinjector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;
import java.util.Optional;

public record ReplaceFully(Optional<LoadPredicate> predicate, ResourceKey<LevelStem> dimension, int priority, HolderSet<Biome> targets, Holder<Biome> replacement) implements BiomeInjector {
	public static final MapCodec<ReplaceFully> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LoadPredicate.FIELD_CODEC.forGetter(ReplaceFully::predicate),
		BiomeInjector.DIMENSION_CODEC.forGetter(ReplaceFully::dimension),
		BiomeInjector.PRIORITY_CODEC.forGetter(ReplaceFully::priority),
		Biome.LIST_CODEC.fieldOf("targets").forGetter(ReplaceFully::targets),
		Biome.CODEC.fieldOf("replacement").forGetter(ReplaceFully::replacement)
	).apply(i, ReplaceFully::new));
	
	@Override
	public List<Holder<Biome>> possibleBiomes() {
		return List.of(this.replacement);
	}
	
	@Override
	public MapCodec<? extends BiomeInjector> codec() {
		return CODEC;
	}
}
