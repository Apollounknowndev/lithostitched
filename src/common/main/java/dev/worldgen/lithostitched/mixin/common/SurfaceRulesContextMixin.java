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

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(SurfaceRules.Context.class)
public class SurfaceRulesContextMixin implements ContextAccessor {
    @Shadow @Final SurfaceSystem system;
    @Shadow @Final ChunkAccess chunk;
    @Shadow Supplier<Holder<Biome>> biome;
    @Shadow int blockX;
    @Shadow int blockY;
    @Shadow int blockZ;
    @Shadow int stoneDepthBelow;

    @Override
    public SurfaceSystem getSystem() {
        return this.system;
    }

    @Override
    public ChunkAccess getChunk() {
        return this.chunk;
    }

    @Override
    public Holder<Biome> getBiome() {
        return this.biome.get();
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

    @Inject(method = "<init>", at = @At("RETURN"))
    public void instantiateConditions(SurfaceSystem system, RandomState randomState, ChunkAccess chunk, NoiseChunk noiseChunk, Function<BlockPos, Holder<Biome>> biomeGetter, Registry<Biome> biomeRegistry, WorldGenerationContext context, CallbackInfo ci) {
        SurfaceRules.Context $this = (SurfaceRules.Context) (Object) this;
    }
}