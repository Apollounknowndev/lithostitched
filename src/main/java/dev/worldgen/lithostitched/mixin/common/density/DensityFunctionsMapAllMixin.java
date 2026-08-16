package dev.worldgen.lithostitched.mixin.common.density;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.MapAllCache;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {
    "net.minecraft.world.level.levelgen.DensityFunctions$Ap2",
    "net.minecraft.world.level.levelgen.DensityFunctions$RangeChoice",
    "net.minecraft.world.level.levelgen.DensityFunctions$ShiftedNoise",
    "net.minecraft.world.level.levelgen.DensityFunctions$Spline"
})
public class DensityFunctionsMapAllMixin {
    
    @WrapMethod(method = "mapAll")
    private DensityFunction modulation$cacheMapAllSafely(DensityFunction.Visitor visitor, Operation<DensityFunction> original) {
        DensityFunction self = (DensityFunction) this;
        
        DensityFunction cached = MapAllCache.get(visitor, self);
        if (cached != null) {
            return cached;
        }
        
        MapAllCache.push();
        
        try {
            DensityFunction result = original.call(visitor);
            
            MapAllCache.put(visitor, self, result);
            return result;
        } finally {
            MapAllCache.pop();
        }
    }
}