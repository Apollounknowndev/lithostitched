package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class WrappedMarkerDensityFunction implements MarkerFunction {
    public static final MapCodec<WrappedMarkerDensityFunction> CODEC = MapCodec.unit(new WrappedMarkerDensityFunction());
    
    @Override
    public @NotNull MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
