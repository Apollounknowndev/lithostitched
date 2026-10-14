package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.worldgen.processor.LithostitchedProcessorLists;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WoodlandMansionStructure.class)
public class WoodlandMansionStructureMixin {
	@WrapOperation(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		),
		method = "afterPlace"
	)
	private static boolean applyProcessorToWoodlandMansionExtension(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, @Local(ordinal = 0, argsOnly = true) BoundingBox chunkBB) {
		var processorList = NonTemplateProcessorHelper.getProcessor(level, LithostitchedProcessorLists.DESERT_TEMPLE);
		if (processorList.isEmpty()) {
			return operation.call(level, pos, state, flags);
		}
		
		BlockState processedState = NonTemplateProcessorHelper.processBlock(chunkBB.getCenter(), level, pos, state, processorList.get());
		if (processedState != null) {
			return operation.call(level, pos, processedState, flags);
		}
		return false;
	}
}
