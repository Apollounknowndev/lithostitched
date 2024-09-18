package dev.worldgen.lithostitched.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class WrappedMarkerDensityFunction implements DensityFunction.SimpleFunction {
    public static final KeyDispatchDataCodec<WrappedMarkerDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new WrappedMarkerDensityFunction()));

    @Override
    public double compute(FunctionContext context) {
        throw new IllegalStateException("Wrapped marker density function should never be computed!");
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return 0;
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
