package dev.worldgen.lithostitched.impl.worldgen.densityfunction.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.worldgen.feature.DensityFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record AbsoluteAnchorFunction(DensityFunction input) implements DensityFunction {
	public static final MapCodec<AbsoluteAnchorFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		DensityFunction.CODEC.fieldOf("input").forGetter(AbsoluteAnchorFunction::input)
	).apply(i, AbsoluteAnchorFunction::new));
	
	@Override
	public DensitySampler compileSampler(CompileContext context) {
		return new AbsoluteAnchorFunction.Sampler(this.input.compileSampler(context));
	}
	
	@Override
	public DensityFunction rewriteChildren(DfRewriteRule rule) {
		DensityFunction input = rule.rewrite(this.input);
		return input == this.input ? this : new AbsoluteAnchorFunction(input);
	}
	
	@Override
	public Interval range() {
		return this.input.range();
	}
	
	@Override
	public @Axes int domainAxes() {
		return this.input.domainAxes();
	}
	
	@Override
	public MapCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
	
	public record Sampler(DensitySampler input) implements DensitySampler {
		@Override
		public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
			Vec3i absolutePos = context.getField(DensityFeature.ABSOLUTE_POS);
			if (absolutePos != null) {
				this.input.sampleVolume(context, outputBuffer, new DensityVolume(
					volume.sizeX(), volume.sizeY(), volume.sizeZ(),
					absolutePos.getX(), absolutePos.getY(), absolutePos.getZ(),
					volume.stepBlockX(), volume.stepBlockY(), volume.stepBlockZ()
				));
			} else {
				this.input.sampleVolume(context, outputBuffer, volume);
			}
		}
		
		@Override
		public float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ) {
			Vec3i absolutePos = context.getField(DensityFeature.ABSOLUTE_POS);
			if (absolutePos != null) {
				return this.input.sampleValue(context, absolutePos.getX(), absolutePos.getY(), absolutePos.getZ());
			} else {
				return this.input.sampleValue(context, blockX, blockY, blockZ);
			}
		}
	}
}
