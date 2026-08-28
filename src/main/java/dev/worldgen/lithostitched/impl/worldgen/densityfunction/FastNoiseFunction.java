package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.synth.Noise;

public record FastNoiseFunction(Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
    public static final MapCodec<FastNoiseFunction> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
        RegistryCodecs.holder(LithostitchedRegistries.FAST_NOISE_CONFIG).fieldOf("config").forGetter(FastNoiseFunction::config),
        Codec.DOUBLE.optionalFieldOf("xz_scale", 1.0).forGetter(FastNoiseFunction::xzScale),
        Codec.DOUBLE.optionalFieldOf("y_scale", 1.0).forGetter(FastNoiseFunction::yScale),
        DensityFunction.CODEC.optionalFieldOf("shift_x", DensityFunctions.zero()).forGetter(FastNoiseFunction::shiftX),
        DensityFunction.CODEC.optionalFieldOf("shift_y", DensityFunctions.zero()).forGetter(FastNoiseFunction::shiftY),
        DensityFunction.CODEC.optionalFieldOf("shift_z", DensityFunctions.zero()).forGetter(FastNoiseFunction::shiftZ)
    ).apply(instance, FastNoiseFunction::new));
    
    @Override
    public DensitySampler compileSampler(CompileContext context) {
        Noise noise = this.config.value();
        if (this.shiftX.equals(DensityFunctions.zero()) && this.shiftY.equals(DensityFunctions.zero()) && this.shiftZ.equals(DensityFunctions.zero())) {
            return new Sampler(noise, this.xzScale, this.yScale);
        }
        
        DensitySampler shiftX = this.shiftX.compileSampler(context);
        DensitySampler shiftZ = this.shiftZ.compileSampler(context);
        if (this.shiftY.equals(DensityFunctions.zero())) {
            return new ShiftedXzSampler(shiftX, shiftZ, noise, this.xzScale, this.yScale);
        }
        
        DensitySampler shiftY = this.shiftY.compileSampler(context);
        return new ShiftedXyzSampler(shiftX, shiftY, shiftZ, noise, this.xzScale, this.yScale);
    }
    
    @Override
    public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction shiftX = rule.rewrite(this.shiftX);
        DensityFunction shiftY = rule.rewrite(this.shiftY);
        DensityFunction shiftZ = rule.rewrite(this.shiftZ);
        return shiftX == this.shiftX && shiftY == this.shiftY && shiftZ == this.shiftZ ? this : new FastNoiseFunction(this.config, this.xzScale, this.yScale, shiftX, shiftY, shiftZ);
    }
    
    @Override
    public Interval range() {
        return this.config.value().range();
    }
    
    @Override
    public @Axes int domainAxes() {
        int axes = 7;
        if (this.yScale == 0.0) {
            axes &= -3;
        }
        
        if (this.xzScale == 0.0) {
            axes &= -6;
        }
        
        return axes | this.shiftX.domainAxes() | this.shiftY.domainAxes() | this.shiftZ.domainAxes();
    }
    
    @Override
    public MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
    
    public record Sampler(Noise noise, double xzScale, double yScale) implements DensitySampler {
        @Override
        public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
            outputBuffer.fill(0.0F);
            this.noise.addToVolume(outputBuffer, volume, this.xzScale, this.yScale, 1.0F);
        }
        
        @Override
        public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
            return this.noise.get(blockX * this.xzScale, blockY * this.yScale, blockZ * this.xzScale);
        }
    }
    
    public record ShiftedXyzSampler(DensitySampler shiftX, DensitySampler shiftY, DensitySampler shiftZ, Noise noise, double xzScale, double yScale)
        implements DensitySampler {
        @Override
        public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
            this.shiftX.sampleVolume(context, outputBuffer, volume);
            
            try (ScopedDensityBuffer shiftYBuffer = context.acquireBuffer(volume)) {
                this.shiftY.sampleVolume(context, shiftYBuffer, volume);
                
                try (ScopedDensityBuffer shiftZBuffer = context.acquireBuffer(volume)) {
                    this.shiftZ.sampleVolume(context, shiftZBuffer, volume);
                    int index = 0;
                    
                    for (int z = 0; z < volume.sizeZ(); z++) {
                        double baseNoiseZ = volume.blockZ(z) * this.xzScale;
                        
                        for (int x = 0; x < volume.sizeX(); x++) {
                            double baseNoiseX = volume.blockX(x) * this.xzScale;
                            
                            for (int y = 0; y < volume.sizeY(); y++) {
                                double noiseX = baseNoiseX + outputBuffer.get(index);
                                double noiseY = volume.blockY(y) * this.yScale + shiftYBuffer.get(index);
                                double noiseZ = baseNoiseZ + shiftZBuffer.get(index);
                                outputBuffer.set(index, this.noise.get(noiseX, noiseY, noiseZ));
                                index++;
                            }
                        }
                    }
                }
            }
        }
        
        @Override
        public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
            double x = blockX * this.xzScale + this.shiftX.sampleValue(context, blockX, blockY, blockZ);
            double y = blockY * this.yScale + this.shiftY.sampleValue(context, blockX, blockY, blockZ);
            double z = blockZ * this.xzScale + this.shiftZ.sampleValue(context, blockX, blockY, blockZ);
            return this.noise.get(x, y, z);
        }
    }
    
    public record ShiftedXzSampler(DensitySampler shiftX, DensitySampler shiftZ, Noise noise, double xzScale, double yScale) implements DensitySampler {
        @Override
        public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
            this.shiftX.sampleVolume(context, outputBuffer, volume);
            
            try (ScopedDensityBuffer shiftZBuffer = context.acquireBuffer(volume)) {
                this.shiftZ.sampleVolume(context, shiftZBuffer, volume);
                int index = 0;
                
                for (int z = 0; z < volume.sizeZ(); z++) {
                    double baseNoiseZ = volume.blockZ(z) * this.xzScale;
                    
                    for (int x = 0; x < volume.sizeX(); x++) {
                        double baseNoiseX = volume.blockX(x) * this.xzScale;
                        
                        for (int y = 0; y < volume.sizeY(); y++) {
                            double noiseX = baseNoiseX + outputBuffer.get(index);
                            double noiseY = volume.blockY(y) * this.yScale;
                            double noiseZ = baseNoiseZ + shiftZBuffer.get(index);
                            outputBuffer.set(index, this.noise.get(noiseX, noiseY, noiseZ));
                            index++;
                        }
                    }
                }
            }
        }
        
        @Override
        public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
            double x = blockX * this.xzScale + this.shiftX.sampleValue(context, blockX, blockY, blockZ);
            double y = blockY * this.yScale;
            double z = blockZ * this.xzScale + this.shiftZ.sampleValue(context, blockX, blockY, blockZ);
            return this.noise.get(x, y, z);
        }
    }
}
