package dev.worldgen.lithostitched.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class WrappedMarkerDensityFunction implements MarkerFunction {
    public static final KeyDispatchDataCodec<WrappedMarkerDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new WrappedMarkerDensityFunction()));

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
