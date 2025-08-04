package dev.worldgen.lithostitched.mixin.common.bandlands;

import dev.worldgen.lithostitched.duck.ContextAccessor;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SurfaceRules.Context.class)
public class SurfaceRulesContextMixin implements ContextAccessor {
    @Shadow @Final
    SurfaceSystem system;

    @Override
    public SurfaceSystem getSystem() {
        return this.system;
    }
}