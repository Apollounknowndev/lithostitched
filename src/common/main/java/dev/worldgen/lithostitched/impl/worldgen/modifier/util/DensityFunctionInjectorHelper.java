package dev.worldgen.lithostitched.impl.worldgen.modifier.util;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.msrandom.multiplatform.annotations.Expect;

public class DensityFunctionInjectorHelper {
    @Expect
    public static DensityFunction wrap(final DensityFunction wrapped, DensityFunction wrapper);
    
    @Expect
    private static boolean isMarker(DensityFunction df);
}
