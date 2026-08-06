package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record LithostitchedFeatureConfig(Holder<PlacedFeature> feature, Identifier jigsawName, Identifier targetName) {
	private static final Identifier DEFAULT_JIGSAW_NAME = Identifier.withDefaultNamespace("bottom");
	private static final Identifier DEFAULT_TARGET_NAME = Identifier.withDefaultNamespace("empty");
	public static final MapCodec<LithostitchedFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		PlacedFeature.CODEC.fieldOf("feature").forGetter(LithostitchedFeatureConfig::feature),
		Identifier.CODEC.fieldOf("jigsaw_name").orElse(DEFAULT_JIGSAW_NAME).forGetter(LithostitchedFeatureConfig::jigsawName),
		Identifier.CODEC.fieldOf("target_name").orElse(DEFAULT_TARGET_NAME).forGetter(LithostitchedFeatureConfig::targetName)
	).apply(i, LithostitchedFeatureConfig::new));
}
