package dev.worldgen.lithostitched.worldgen.biomeinjector.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;

public record Region(Optional<ResourceKey<Region>> name, ResourceKey<LevelStem> dimension, int weight) {
	public static final Codec<ResourceKey<Region>> KEY_CODEC = ResourceKey.codec(LithostitchedRegistryKeys.REGION);
	public static final Codec<Region> CODEC = RecordCodecBuilder.create(i -> i.group(
		KEY_CODEC.optionalFieldOf("name").forGetter(Region::name),
		BiomeInjector.DIMENSION_CODEC.forGetter(Region::dimension),
		ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(Region::weight)
	).apply(i, Region::new));
}
