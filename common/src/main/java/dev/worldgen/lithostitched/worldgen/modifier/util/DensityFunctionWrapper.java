package dev.worldgen.lithostitched.worldgen.modifier.util;

import dev.worldgen.lithostitched.worldgen.densityfunction.MarkerFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.MergedDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.OriginalMarkerDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class DensityFunctionWrapper {
    public static DensityFunction wrap(DensityFunction wrapped, DensityFunction wrapper) {
        if (wrapped instanceof MergedDensityFunction merged) {
            final DensityFunction original = merged.original();
            final DensityFunction input = merged.merged();
            return new MergedDensityFunction(input, wrapper.mapAll(value -> {
                if (value instanceof DensityFunctions.HolderHolder holderHolder && holderHolder.function().value() instanceof MarkerFunction marker) {
                    if (marker instanceof OriginalMarkerDensityFunction) {
                        return original;
                    }
                    return input;
                }

                return value;
            }));
        }

        final DensityFunction input = wrapped;
        return new MergedDensityFunction(input, wrapper.mapAll(value -> {
            if (value instanceof DensityFunctions.HolderHolder holderHolder && holderHolder.function().value() instanceof MarkerFunction) {
                return input;
            }

            return value;
        }));
    }
}
