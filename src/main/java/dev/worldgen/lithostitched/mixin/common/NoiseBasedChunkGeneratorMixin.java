package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.duck.SurfaceSystemAccessor;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
	@WrapOperation(
		method = "spawnOriginalMobs",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos;atY(I)Lnet/minecraft/core/BlockPos;"
		)
	)
	private BlockPos fixSampledBiomeY(BlockPos pos, int y, Operation<BlockPos> original, @Local(argsOnly = true, ordinal = 0) WorldGenRegion worldGenRegion) {
		return pos.atY(worldGenRegion.getChunk(pos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));
	}
	
	@Inject(method = "buildTerrain", at = @At("HEAD"))
	private void fillBands(ChunkAccess chunk, Blender blender, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, @Nullable WorldGenRegion carverBiomeRegion, Set<Holder<Biome>> possibleBiomes, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
		HolderLookup.RegistryLookup<Bandlands> registry = carverBiomeRegion.registryAccess().lookupOrThrow(LithostitchedRegistries.BANDLANDS);
		
		registry.listElements().forEach(holder -> holder.value().fillBands(
			((SurfaceSystemAccessor)randomState.surfaceSystem()).getNoiseRandom().fromHashOf(Identifier.withDefaultNamespace("clay_bands")))
		);
	}
}
