package dev.worldgen.lithostitched.impl.worldgen.modifier.util;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MarkerFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerDensityFunction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class DensityFunctionInjectorHelper {
    public static DensityFunction wrap(final DensityFunction wrapped, DensityFunction wrapper) {
        if (wrapped instanceof MergedDensityFunction merged) {
            final DensityFunction original = merged.original();
            return new MergedDensityFunction(original, wrapped, wrapper.mapAll(value -> {
                if (isMarker(value)) {
                    if (value instanceof OriginalMarkerDensityFunction) {
                        return original;
                    }
                    return wrapped;
                }
                
                return value;
            }));
        }
        
        return new MergedDensityFunction(wrapped, wrapped, wrapper.mapAll(value -> {
            if (isMarker(value)) {
                return wrapped;
            }
            
            return value;
        }));
    }
    
    private static boolean isMarker(DensityFunction df) {
        if (df instanceof DensityFunctions.HolderHolder(Holder<DensityFunction> function)) {
            df = function.value();
        }
        return df instanceof MarkerFunction;
    }
}
