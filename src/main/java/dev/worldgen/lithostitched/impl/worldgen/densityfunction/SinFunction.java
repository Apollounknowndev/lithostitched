package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record SinFunction(DensityFunction input) implements DensityFunction {
    public static final MapCodec<SinFunction> CODEC = DensityFunction.CODEC.fieldOf("input").xmap(SinFunction::new, SinFunction::input);
    
    @Override
    public DensitySampler compileSampler(CompileContext context) {
        return new CosFunction.CosSampler(this.input.compileSampler(context));
    }
    
    @Override
    public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction input = rule.rewrite(this.input);
        return input == this.input ? this : new CosFunction(input);
    }
    
    @Override
    public Interval range() {
        return Interval.of(-1, 1);
    }
    
    @Override
    public @Axes int domainAxes() {
        return this.input.domainAxes();
    }
    
    @Override
    public MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
    
    public record SinSampler(DensitySampler input) implements DensitySampler {
        @Override
        public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
            this.input.sampleVolume(context, outputBuffer, volume);
            
            for (int i = 0; i < outputBuffer.size(); i++) {
                outputBuffer.set(i, (float)Math.sin(outputBuffer.get(i)));
            }
        }
        
        @Override
        public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
            return (float)Math.sin(this.input.sampleValue(context, blockX, blockY, blockZ));
        }
    }
}

