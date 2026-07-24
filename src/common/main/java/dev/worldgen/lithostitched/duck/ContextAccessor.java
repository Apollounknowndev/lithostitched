package dev.worldgen.lithostitched.duck;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;

public interface ContextAccessor {
    SurfaceSystem getSystem();
    ChunkAccess getChunk();
    NoiseChunk getNoiseChunk();
    RandomState getRandomState();
    int getStoneDepthBelow();

    int getX();
    int getY();
    int getZ();
    
    default Vec3i getPos() {
        return new Vec3i(getX(), getY(), getZ());
    }
}