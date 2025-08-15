package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.worldgen.surface.conditions.LithostitchedSurfaceConditions;
import dev.worldgen.lithostitched.worldgen.surface.technical.IContextExtension;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.function.Function;

@Mixin(SurfaceRules.Context.class)
public final class ContextMixin implements IContextExtension {
    // Shadowed variables from Context
    @Shadow
    long lastUpdateXZ;
    @Shadow
    public int blockX;
    @Shadow
    public int blockZ;
    @Shadow
    @Final
    public ChunkAccess chunk;
    @Shadow
    @Final
    public RandomState randomState;
    // Variables for the cached conditions
    @Unique
    @SuppressWarnings("all")
    SurfaceRules.Condition cliff, flat, flatLiquid, aboveWater;
    // Caches for heightmaps & noises
    @Unique
    @SuppressWarnings("all")
    private int oceanHeightmapDepthCache = -Integer.MAX_VALUE;
    @Unique
    @SuppressWarnings("all")
    private IdentityHashMap<ResourceKey<NormalNoise.NoiseParameters>, Double> noiseCache = new IdentityHashMap<>();
    // Update timers for heightmaps, biomes, and noises
    @Unique
    @SuppressWarnings("all")
    private long lastUpdateHeightmapDepth;
    @Unique
    @SuppressWarnings("all")
    private long lastUpdateNoises;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void instantiateConditions(SurfaceSystem system,
                                      RandomState randomState,
                                      ChunkAccess chunk,
                                      NoiseChunk noiseChunk,
                                      Function<BlockPos, Holder<Biome>> biomeGetter,
                                      Registry<Biome> biomeRegistry,
                                      WorldGenerationContext context,
                                      CallbackInfo ci) {
        SurfaceRules.Context self = (SurfaceRules.Context) (Object) this;
        cliff = new LithostitchedSurfaceConditions.CliffCondition(self);
        flat = new LithostitchedSurfaceConditions.FlatCondition(self);
        flatLiquid = new LithostitchedSurfaceConditions.FlatLiquidCondition(self);
        aboveWater = new LithostitchedSurfaceConditions.LandTopLayerCondition(self);
    }

    @Override
    public SurfaceRules.Condition lithostitched$getCliff() {
        return cliff;
    }

    @Override
    public SurfaceRules.Condition lithostitched$getFlat() {
        return flat;
    }

    @Override
    public SurfaceRules.Condition lithostitched$getFlatLiquid() {
        return flatLiquid;
    }

    @Override
    public SurfaceRules.Condition lithostitched$getLandTopLayer() {
        return aboveWater;
    }

    @Override
    public int lithostitched$getOceanHeightmapDepth() {
        if (lastUpdateXZ != lastUpdateHeightmapDepth) {
            oceanHeightmapDepthCache = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockX, blockZ);
            lastUpdateHeightmapDepth = lastUpdateXZ;
        }
        return oceanHeightmapDepthCache;
    }

    @Override
    public double lithostitched$getCachedNoise(ResourceKey<NormalNoise.NoiseParameters> noise) {
        if (lastUpdateXZ != lastUpdateNoises) {
            noiseCache.clear();
            lastUpdateNoises = lastUpdateXZ;
        }
        return noiseCache.computeIfAbsent(noise, v -> randomState.getOrCreateNoise(noise).getValue(blockX, 0.0, blockZ));
    }
}

