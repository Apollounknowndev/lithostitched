package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Collection;
import java.util.Optional;

public record Region(Optional<ResourceKey<Region>> name, ResourceKey<LevelStem> dimension, HolderSet<Biome> biomes, int weight) {
	public static final Codec<ResourceKey<Region>> KEY_CODEC = ResourceKey.codec(LithostitchedRegistries.REGION);
	public static final Codec<Region> CODEC = RecordCodecBuilder.create(i -> i.group(
		BiomeInjector.DIMENSION_CODEC.forGetter(Region::dimension),
		ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(Region::weight),
		Biome.LIST_CODEC.fieldOf("biomes").forGetter(Region::biomes)
	).apply(i, Region::create));
	
	public static Region create(ResourceKey<LevelStem> level, int weight, HolderSet<Biome> biomes) {
		return new Region(Optional.empty(), level, biomes, weight);
	}
	
	public static Region create(ResourceKey<Region> key, ResourceKey<Level> level, HolderSet<Biome> biomes, int weight) {
		return new Region(Optional.of(key), Registries.levelToLevelStem(level), biomes, weight);
	}
}
