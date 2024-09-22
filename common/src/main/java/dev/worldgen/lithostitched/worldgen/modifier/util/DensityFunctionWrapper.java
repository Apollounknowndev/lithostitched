package dev.worldgen.lithostitched.worldgen.modifier.util;

import dev.worldgen.lithostitched.worldgen.densityfunction.MarkerFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.MergedDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.densityfunction.ReferenceMarkerDensityFunction;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import org.jetbrains.annotations.Nullable;

public class DensityFunctionWrapper {
    public static DensityFunction wrap(DensityFunction wrapped, DensityFunction wrapper, @Nullable NoiseRouter router) {
        if (wrapped instanceof MergedDensityFunction merged) {
            final DensityFunction original = merged.original();
            final DensityFunction input = merged.merged();
            return new MergedDensityFunction(input, wrapper.mapAll(value -> {
                if (value instanceof DensityFunctions.HolderHolder holderHolder && holderHolder.function().value() instanceof MarkerFunction marker) {
                    if (marker instanceof OriginalMarkerDensityFunction) {
                        return original;
                    }
                    if (marker instanceof ReferenceMarkerDensityFunction reference) {
                        if (router == null) throw new IllegalStateException("Reference markers cannot be used when directly modifying density functions!");
                        return reference.target().get(router);
                    }

                    return input;
                }

                return value;
            }));
        }

        final DensityFunction input = wrapped;
        return new MergedDensityFunction(input, wrapper.mapAll(value -> {
            if (value instanceof DensityFunctions.HolderHolder holderHolder && holderHolder.function().value() instanceof MarkerFunction marker) {
                if (marker instanceof ReferenceMarkerDensityFunction reference) {
                    if (router == null) throw new IllegalStateException("Reference markers cannot be used when directly modifying density functions!");
                    return reference.target().get(router);
                }

                return input;
            }

            return value;
        }));
    }
}
