package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class OriginalMarkerDensityFunction implements MarkerFunction {
    public static final MapCodec<OriginalMarkerDensityFunction> CODEC = MapCodec.unit(new OriginalMarkerDensityFunction());
    
    @Override
    public @NotNull MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
