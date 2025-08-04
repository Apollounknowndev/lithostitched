package dev.worldgen.lithostitched.duck;

import net.minecraft.world.level.levelgen.SurfaceSystem;

public interface ContextAccessor {
    SurfaceSystem getSystem();
}