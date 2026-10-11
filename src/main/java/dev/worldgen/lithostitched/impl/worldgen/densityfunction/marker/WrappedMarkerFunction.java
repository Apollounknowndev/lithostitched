package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import org.jetbrains.annotations.NotNull;

public non-sealed class WrappedMarkerFunction implements MarkerFunction {
    public static final MapCodec<WrappedMarkerFunction> CODEC = MapCodec.unit(new WrappedMarkerFunction());
    
    @Override
    public @NotNull MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
