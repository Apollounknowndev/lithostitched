package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import org.jetbrains.annotations.NotNull;

public non-sealed class OriginalMarkerFunction implements MarkerFunction {
    public static final MapCodec<OriginalMarkerFunction> CODEC = MapCodec.unit(new OriginalMarkerFunction());
    
    @Override
    public @NotNull MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
