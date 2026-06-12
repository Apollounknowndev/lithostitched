package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface MarkerFunction extends DensityFunction.SimpleFunction {
    @Override
    default double compute(FunctionContext context) {
        throw new IllegalStateException("Marker density function should never be computed!");
    }

    @Override
    default double minValue() {
        return 0;
    }

    @Override
    default double maxValue() {
        return 0;
    }
}
