package dev.worldgen.lithostitched.mixin.common.bandlands;

import dev.worldgen.lithostitched.impl.duck.SurfaceSystemAccessor;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

// TODO: Fix custom bandlands
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    /*@Inject(method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/levelgen/blending/Blender;Ljava/util/Set;)V", at = @At("HEAD"))
    private void fillBands(ChunkAccess protoChunk, WorldGenerationContext context, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, Blender blender, @Nullable Set<Holder<Biome>> possibleBiomes, CallbackInfo ci) {
        HolderLookup.RegistryLookup<Bandlands> registry = context.registryAccess().lookupOrThrow(LithostitchedRegistries.BANDLANDS);

        registry.listElements().forEach(holder -> holder.value().fillBands(
                ((SurfaceSystemAccessor)randomState.surfaceSystem()).getNoiseRandom().fromHashOf(Identifier.withDefaultNamespace("clay_bands")))
        );
    }*/
}
