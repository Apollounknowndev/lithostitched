package dev.worldgen.lithostitched.duck;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;

public interface ContextAccessor {
    SurfaceSystem lithostitched$getSystem();
    ChunkAccess lithostitched$getChunk();
    NoiseChunk lithostitched$getNoiseChunk();
    RandomState lithostitched$getRandomState();
    int lithostitched$getStoneDepthBelow();

    int lithostitched$getX();
    int lithostitched$getY();
    int lithostitched$getZ();
    
    default Vec3i getPos() {
        return new Vec3i(lithostitched$getX(), lithostitched$getY(), lithostitched$getZ());
    }
}