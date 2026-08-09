package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
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
			target = "Lnet/minecraft/core/BlockPos;atY(I)Lnet/minecraft/core/BlockPos;"
		)
	)
	private BlockPos fixSampledBiomeY(BlockPos pos, int y, Operation<BlockPos> original, @Local(argsOnly = true, ordinal = 0) WorldGenRegion worldGenRegion) {
		return pos.atY(worldGenRegion.getChunk(pos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));
	}
}
