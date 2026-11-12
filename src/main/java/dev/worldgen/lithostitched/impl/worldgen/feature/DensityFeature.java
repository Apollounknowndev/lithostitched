package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.util.DensitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record DensityFeature(Vec3i min, Vec3i max, DensityFunction densityFunction, DensitySelector<Holder<BlockStateProvider>> selector) implements Feature {
	public static final ContextKey<Vec3i> ABSOLUTE_POS = new ContextKey<>(Lithostitched.id("absolute_pos"));
	public static final ContextKey<Vec3i> RELATIVE_POS = new ContextKey<>(Lithostitched.id("relative_pos"));
	
	private static final Codec<Vec3i> XZ_BOUND_OFFSET_CODEC = Vec3i.CODEC.validate(vec -> {
		if (Math.abs(vec.getX()) <= 32 && Math.abs(vec.getZ()) <= 32) {
			return DataResult.success(vec);
		}
		return DataResult.error(() -> "Position out of range, expected at most 32 on x/z: " + vec);
	});
	
	public static final MapCodec<DensityFeature> CODEC = RecordCodecBuilder.<DensityFeature>mapCodec(instance -> instance.group(
		XZ_BOUND_OFFSET_CODEC.fieldOf("min_offset").forGetter(DensityFeature::min),
		XZ_BOUND_OFFSET_CODEC.fieldOf("max_offset").forGetter(DensityFeature::max),
		DensityFunction.CODEC.fieldOf("density_function").forGetter(DensityFeature::densityFunction),
		DensitySelector.codec(BlockStateProvider.CODEC).fieldOf("selector").forGetter(DensityFeature::selector)
	).apply(instance, DensityFeature::new)).validate(
		f -> f.min.getX() <= f.max.getX() && f.min.getY() <= f.max.getY() && f.min.getZ() <= f.max.getZ()
			? DataResult.success(f)
			: DataResult.error(() -> "min bound cannot be larger than max bound")
	);
	
	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}
	
	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		BlockPos bottomCorner = origin.offset(this.min);
		
		DensitySampler sampler = level.getLevel().getChunkSource().randomState().getSampler(this.densityFunction);
		SamplerContext context = SamplerContext.builder()
			.enableCaches()
			.setUserFields(getContextMap(bottomCorner))
			.build();
		
		int sizeX = this.max.getX() - this.min.getX();
		int sizeY = this.max.getY() - this.min.getY();
		int sizeZ = this.max.getZ() - this.min.getZ();
		
		DensityVolume volume = new DensityVolume(sizeX, sizeY, sizeZ, bottomCorner.getX(), bottomCorner.getY(), bottomCorner.getZ());
		DensityBuffer buffer = DensityBuffer.createUnpooled(volume.size());
		sampler.sampleVolume(context, buffer, volume);
		
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					int index = volume.indexUnchecked(x, y, z);
					
					float density = buffer.get(index);
					BlockPos pos = bottomCorner.offset(x, y, z);
					BlockState state = this.selector.pick(density).value().getOptionalState(level, random, pos);
					if (state != null) {
						level.setBlock(pos, state, 3);
					}
				}
			}
		}
		
		return true;
	}
	
	private ContextMap getContextMap(BlockPos bottomCorner) {
		return ContextMap.builder()
			.set(ABSOLUTE_POS, bottomCorner)
			.set(RELATIVE_POS, this.min)
			.build();
	}
}
