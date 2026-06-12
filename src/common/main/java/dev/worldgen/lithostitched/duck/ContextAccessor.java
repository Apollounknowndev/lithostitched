package dev.worldgen.lithostitched.duck;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.SurfaceSystem;

public interface ContextAccessor {
    SurfaceSystem getSystem();
    ChunkAccess getChunk();
    int getStoneDepthBelow();

    int getX();
    int getY();
    int getZ();
}