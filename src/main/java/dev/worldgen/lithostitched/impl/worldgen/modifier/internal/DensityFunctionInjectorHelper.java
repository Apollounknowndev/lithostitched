package dev.worldgen.lithostitched.impl.worldgen.modifier.internal;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MarkerFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerFunction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

public class DensityFunctionInjectorHelper {
    public static DensityFunction wrap(final DensityFunction wrapped, DensityFunction wrapper) {
        if (wrapped instanceof MergedFunction merged) {
            final DensityFunction original = merged.original();
            return new MergedFunction(original, wrapped, wrapper.rewriteChildren(new ApplyWrappedAndOriginalRule(original, wrapped)));
        }
        
        return new MergedFunction(wrapped, wrapped, wrapper.rewriteChildren(new ApplyWrappedRule(wrapped)));
    }
    
    private static boolean isMarker(DensityFunction df) {
        if (df instanceof DensityFunctions.HolderHolder(Holder<DensityFunction> function)) {
            return isMarker(function.value());
        }
        return df instanceof MarkerFunction;
    }
    
    public record ApplyWrappedAndOriginalRule(DensityFunction original, DensityFunction wrapped) implements DfRewriteRule {
        @Override
        public DensityFunction rewrite(DensityFunction function) {
            if (isMarker(function)) {
                return function instanceof OriginalMarkerFunction ? this.original : this.wrapped;
            }
            
            return function.rewriteChildren(this);
        }
    }
    
    public record ApplyWrappedRule(DensityFunction wrapped) implements DfRewriteRule {
        @Override
        public DensityFunction rewrite(DensityFunction function) {
            if (isMarker(function)) {
                return this.wrapped;
            }
            
            return function.rewriteChildren(this);
        }
    }
}
