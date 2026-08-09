package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.duck.ContextAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SurfaceRules.Context.class)
public class SurfaceRulesContextMixin implements ContextAccessor {
    @Shadow @Final SurfaceSystem system;
    @Shadow @Final ChunkAccess chunk;
    @Shadow int blockX;
    @Shadow int blockY;
    @Shadow int blockZ;
    @Shadow int stoneDepthBelow;
    @Shadow NoiseChunk noiseChunk;
    
    @Shadow private RandomState randomState;
    
    @Override
    public SurfaceSystem getSystem() {
        return this.system;
    }

    @Override
    public ChunkAccess getChunk() {
        return this.chunk;
    }
    
    @Override
    public NoiseChunk getNoiseChunk() {
        return this.noiseChunk;
    }
    
    @Override
    public RandomState getRandomState() {
        return this.randomState;
    }
    
    @Override
    public int getStoneDepthBelow() {
        return this.stoneDepthBelow;
    }

    @Override
    public int getX() {
        return this.blockX;
    }

    @Override
    public int getY() {
        return this.blockY;
    }

    @Override
    public int getZ() {
        return this.blockZ;
    }
}