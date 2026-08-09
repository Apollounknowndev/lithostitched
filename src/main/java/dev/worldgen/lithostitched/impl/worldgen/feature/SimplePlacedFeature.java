package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record SimplePlacedFeature(Holder<PlacedFeature> feature) implements Feature {
	public static final MapCodec<SimplePlacedFeature> CODEC = PlacedFeature.CODEC.fieldOf("feature").xmap(SimplePlacedFeature::new, SimplePlacedFeature::feature);
	
	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		return this.feature.value().place(level, chunkGenerator, random, origin);
	}
	
	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}
}
