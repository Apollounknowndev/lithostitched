package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

public interface MarkerFunction extends DensityFunction {
    @Override
    default DensitySampler compileSampler(final CompileContext context) {
        throw new IllegalStateException("Marker density function should never be compiled!");
    }
    
    @Override
    default DensityFunction rewriteChildren(final DfRewriteRule rule) {
        return this;
    }
    
    @Override
    default Interval range() {
        return Interval.ofExact(0);
    }
    
    @Override
    default @DensityFunction.Axes int domainAxes() {
        return 0;
    }
}
