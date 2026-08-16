package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record LithostitchedFeatureConfig(Holder<PlacedFeature> feature, ResourceLocation jigsawName, ResourceLocation targetName) {
	private static final ResourceLocation DEFAULT_JIGSAW_NAME = ResourceLocation.withDefaultNamespace("bottom");
	private static final ResourceLocation DEFAULT_TARGET_NAME = ResourceLocation.withDefaultNamespace("empty");
	public static final MapCodec<LithostitchedFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		PlacedFeature.CODEC.fieldOf("feature").forGetter(LithostitchedFeatureConfig::feature),
		ResourceLocation.CODEC.fieldOf("jigsaw_name").orElse(DEFAULT_JIGSAW_NAME).forGetter(LithostitchedFeatureConfig::jigsawName),
		ResourceLocation.CODEC.fieldOf("target_name").orElse(DEFAULT_TARGET_NAME).forGetter(LithostitchedFeatureConfig::targetName)
	).apply(i, LithostitchedFeatureConfig::new));
}
