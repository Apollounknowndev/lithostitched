package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.duck.ContextAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public SurfaceSystem lithostitched$getSystem() {
        return this.system;
    }

    @Override
    public ChunkAccess lithostitched$getChunk() {
        return this.chunk;
    }
    
    @Override
    public NoiseChunk lithostitched$getNoiseChunk() {
        return this.noiseChunk;
    }
    
    @Override
    public RandomState lithostitched$getRandomState() {
        return this.randomState;
    }
    
    @Override
    public int lithostitched$getStoneDepthBelow() {
        return this.stoneDepthBelow;
    }

    @Override
    public int lithostitched$getX() {
        return this.blockX;
    }

    @Override
    public int lithostitched$getY() {
        return this.blockY;
    }

    @Override
    public int lithostitched$getZ() {
        return this.blockZ;
    }
}