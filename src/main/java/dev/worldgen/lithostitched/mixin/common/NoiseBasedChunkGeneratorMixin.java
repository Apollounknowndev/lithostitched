package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
	@WrapOperation(
		method = "spawnOriginalMobs",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/WorldGenRegion;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
		)
	)
	private Holder<Biome> fixSampledBiomeY(WorldGenRegion region, BlockPos pos, Operation<Holder<Biome>> operation) {
		return operation.call(region, pos.atY(
			region.getChunk(pos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ())
		));
	}
}
