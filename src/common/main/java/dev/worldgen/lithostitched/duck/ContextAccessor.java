package dev.worldgen.lithostitched.duck;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.SurfaceSystem;

public interface ContextAccessor {
    SurfaceSystem getSystem();
    ChunkAccess getChunk();
    Holder<Biome> getBiome();
    int getStoneDepthBelow();

    int getX();
    int getY();
    int getZ();
}