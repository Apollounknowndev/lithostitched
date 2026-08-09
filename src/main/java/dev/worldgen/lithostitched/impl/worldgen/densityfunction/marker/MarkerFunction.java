package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface MarkerFunction extends DensityFunction {
    @Override
    default float compute(DensityFunction.FunctionContext context) {
        throw new IllegalStateException("Marker density function should never be computed!");
    }
    
    @Override
    default void fillArray(final float[] output, final ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(output, this);
    }
    
    @Override
    default DensityFunction mapChildren(final Visitor visitor) {
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
