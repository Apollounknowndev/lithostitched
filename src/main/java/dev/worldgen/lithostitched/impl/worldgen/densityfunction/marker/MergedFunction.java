package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

/**
 * Holds three density functions, one of which runs.
 * Used for density function wrapping to maintain access to the root density function.
 */
public record MergedFunction(DensityFunction original, DensityFunction wrapped, DensityFunction full) implements DensityFunction {
    public static final MapCodec<DensityFunction> CODEC = DensityFunction.CODEC.xmap(
        df -> df instanceof DensityFunctions.HolderHolder(Holder<DensityFunction> holder) ? holder.value() : df,
        MergedFunction::unwrappedOriginal
    ).fieldOf("original");

    private static DensityFunction unwrappedOriginal(DensityFunction df) {
        return df instanceof MergedFunction merged ? unwrappedOriginal(merged.original()) : df;
    }
    
    @Override
    public DensitySampler compileSampler(CompileContext context) {
        return this.full.compileSampler(context);
    }
    
    @Override
    public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction full = rule.rewrite(this.full);
        return full == this.full ? this : new MergedFunction(original, wrapped, full);
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
