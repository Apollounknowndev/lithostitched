package dev.worldgen.lithostitched.mixin.common.bandlands;

import dev.worldgen.lithostitched.duck.SurfaceSystemAccessor;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.bandlands.Bandlands;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V", at = @At("HEAD"))
    private void fillBands(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess, CallbackInfo ci) {
        HolderLookup.RegistryLookup<Bandlands> registry = region.registryAccess().lookupOrThrow(LithostitchedRegistries.BANDLANDS);

        registry.listElements().forEach(holder -> holder.value().fillBands(
                ((SurfaceSystemAccessor)randomState.surfaceSystem()).getNoiseRandom().fromHashOf(Identifier.withDefaultNamespace("clay_bands")))
        );
    }
}
