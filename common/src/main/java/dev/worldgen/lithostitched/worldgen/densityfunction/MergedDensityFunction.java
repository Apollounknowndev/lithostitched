package dev.worldgen.lithostitched.worldgen.densityfunction;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

/**
 * Holds two density functions, one of which runs.
 * Used for density function wrapping to maintain access to the root density function.
 */
public record MergedDensityFunction(DensityFunction original, DensityFunction merged) implements DensityFunction {
    public static final KeyDispatchDataCodec<MergedDensityFunction> CODEC = KeyDispatchDataCodec.of(
        RecordCodecBuilder.create(instance -> instance.group(
            HOLDER_HELPER_CODEC.fieldOf("original").forGetter(MergedDensityFunction::original),
            HOLDER_HELPER_CODEC.fieldOf("merged").forGetter(MergedDensityFunction::merged)
        ).apply(instance, MergedDensityFunction::new))
    );

    @Override
    public double compute(FunctionContext context) {
        return this.merged.compute(context);
    }

    @Override
    public void fillArray(double[] doubles, ContextProvider contextProvider) {
        this.merged.fillArray(doubles, contextProvider);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return this.merged.mapAll(visitor);
    }

    @Override
    public double minValue() {
        return this.merged.minValue();
    }

    @Override
    public double maxValue() {
        return this.merged.maxValue();
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
