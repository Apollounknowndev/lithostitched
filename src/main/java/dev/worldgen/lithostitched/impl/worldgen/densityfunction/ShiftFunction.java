package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;

public record ShiftFunction(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
	public static final MapCodec<ShiftFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		DensityFunction.CODEC.fieldOf("input").forGetter(ShiftFunction::input),
		DensityFunction.CODEC.fieldOf("shift_x").orElse(DensityFunctions.zero()).forGetter(ShiftFunction::shiftX),
		DensityFunction.CODEC.fieldOf("shift_y").orElse(DensityFunctions.zero()).forGetter(ShiftFunction::shiftY),
		DensityFunction.CODEC.fieldOf("shift_z").orElse(DensityFunctions.zero()).forGetter(ShiftFunction::shiftZ)
	).apply(i, ShiftFunction::new));
	
	@Override
	public DensitySampler compileSampler(CompileContext context) {
		DensitySampler input = this.input.compileSampler(context);
		if (
			this.shiftX instanceof ConstantFunction(float offsetXValue) &&
			this.shiftY instanceof ConstantFunction(float offsetYValue) &&
			this.shiftZ instanceof ConstantFunction(float offsetZValue)
		) {
			return new ConstOffsetSampler(input, (int)offsetXValue, (int)offsetYValue, (int)offsetZValue);
		} else {
			return new Sampler(
				this.input.compileSampler(context),
				this.shiftX.compileSampler(context),
				this.shiftY.compileSampler(context),
				this.shiftZ.compileSampler(context)
			);
		}
	}
	
	@Override
	public DensityFunction rewriteChildren(DfRewriteRule rule) {
		DensityFunction input = rule.rewrite(this.input);
		DensityFunction shiftX = rule.rewrite(this.shiftX);
		DensityFunction shiftY = rule.rewrite(this.shiftY);
		DensityFunction shiftZ = rule.rewrite(this.shiftZ);
		return input == this.input && shiftX == this.shiftX && shiftY == this.shiftY && shiftZ == this.shiftZ ? this : new ShiftFunction(input, shiftX, shiftY, shiftZ);
	}
	
	@Override
	public Interval range() {
		return this.input.range();
	}
	
	@Override
	public @Axes int domainAxes() {
		return this.input.domainAxes() | this.shiftX.domainAxes() | this.shiftY.domainAxes() | this.shiftZ.domainAxes();
	}
	
	@Override
	public MapCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
	
	public record ConstOffsetSampler(DensitySampler input, int offsetX, int offsetY, int offsetZ) implements DensitySampler {
		@Override
		public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
			this.input.sampleVolume(
				context,
				outputBuffer,
				new DensityVolume(
					volume.sizeX(), volume.sizeY(), volume.sizeZ(),
					volume.minBlockX() + this.offsetX, volume.minBlockY() + this.offsetY, volume.minBlockZ() + this.offsetZ,
					volume.stepBlockX(), volume.stepBlockY(), volume.stepBlockZ()
				)
			);
		}
		
		@Override
		public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
			return this.input.sampleValue(context, blockX + this.offsetX, blockY + this.offsetY, blockZ + this.offsetZ);
		}
	}
	
	public record Sampler(DensitySampler input, DensitySampler shiftX, DensitySampler shiftY, DensitySampler shiftZ) implements DensitySampler {
		@Override
		public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
			this.shiftX.sampleVolume(context, outputBuffer, volume);
			
			try (ScopedDensityBuffer shiftYBuffer = context.acquireBuffer(volume)) {
				this.shiftY.sampleVolume(context, shiftYBuffer, volume);
				
				try (ScopedDensityBuffer shiftZBuffer = context.acquireBuffer(volume)) {
					this.shiftZ.sampleVolume(context, shiftZBuffer, volume);
					int index = 0;
					
					for (int z = 0; z < volume.sizeZ(); z++) {
						double baseNoiseZ = volume.blockZ(z);
						
						for (int x = 0; x < volume.sizeX(); x++) {
							double baseNoiseX = volume.blockX(x);
							
							for (int y = 0; y < volume.sizeY(); y++) {
								double noiseX = baseNoiseX + outputBuffer.get(index);
								double noiseY = volume.blockY(y) + shiftYBuffer.get(index);
								double noiseZ = baseNoiseZ + shiftZBuffer.get(index);
								outputBuffer.set(index, this.input.sampleValue(context, (int) noiseX, (int) noiseY, (int) noiseZ));
								index++;
							}
						}
					}
				}
			}
		}
		
		@Override
		public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
			int shiftX = (int) this.shiftX.sampleValue(context, blockX, blockY, blockZ);
			int shiftY = (int) this.shiftY.sampleValue(context, blockX, blockY, blockZ);
			int shiftZ = (int) this.shiftZ.sampleValue(context, blockX, blockY, blockZ);
			return this.input.sampleValue(context, blockX + shiftX, blockY + shiftY, blockZ + shiftZ);
		}
	}
}