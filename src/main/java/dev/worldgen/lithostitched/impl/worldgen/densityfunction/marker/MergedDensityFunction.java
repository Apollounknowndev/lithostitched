package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

/**
 * Holds two density functions, one of which runs.
 * Used for density function wrapping to maintain access to the root density function.
 */
public record MergedDensityFunction(DensityFunction original, DensityFunction wrapped, DensityFunction full) implements DensityFunction {
    public static final MapCodec<DensityFunction> CODEC = DensityFunction.CODEC.xmap(
        df -> df instanceof DensityFunctions.HolderHolder(Holder<DensityFunction> holder) ? holder.value() : df,
        MergedDensityFunction::unwrappedOriginal
    ).fieldOf("original");

    private static DensityFunction unwrappedOriginal(DensityFunction df) {
        return df instanceof MergedDensityFunction merged ? unwrappedOriginal(merged.original()) : df;
    }

    @Override
    public float compute(FunctionContext context) {
        return this.full.compute(context);
    }
    
    @Override
    public void fillArray(float[] output, ContextProvider contextProvider) {
        this.full.fillArray(output, contextProvider);
    }

    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return visitor.apply(this.full);
    }
    
    @Override
    public Interval range() {
        return this.full.range();
    }
    
    @Override
    public @Axes int domainAxes() {
        return this.full.domainAxes();
    }
    
    @Override
    public MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
