package dev.worldgen.lithostitched.worldgen.densityfunction;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.jetbrains.annotations.NotNull;

/**
 * Holds two density functions, one of which runs.
 * Used for density function wrapping to maintain access to the root density function.
 */
public record MergedDensityFunction(DensityFunction original, DensityFunction wrapped, DensityFunction full) implements DensityFunction {
    public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(
        HOLDER_HELPER_CODEC.xmap(
            df -> df instanceof DensityFunctions.HolderHolder hh ? hh.function().value() : df,
            MergedDensityFunction::unwrappedOriginal
        ).fieldOf("original")
    );

    private static DensityFunction unwrappedOriginal(DensityFunction df) {
        return df instanceof MergedDensityFunction merged ? unwrappedOriginal(merged.original()) : df;
    }

    @Override
    public double compute(FunctionContext context) {
        return this.full.compute(context);
    }

    @Override
    public void fillArray(double[] doubles, ContextProvider contextProvider) {
        this.full.fillArray(doubles, contextProvider);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return this.full.mapAll(visitor);
    }

    @Override
    public double minValue() {
        return this.full.minValue();
    }

    @Override
    public double maxValue() {
        return this.full.maxValue();
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
