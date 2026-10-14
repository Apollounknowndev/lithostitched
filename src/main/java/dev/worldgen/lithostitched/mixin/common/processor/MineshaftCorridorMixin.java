package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.tag.LithostitchedBlockTags;
import dev.worldgen.lithostitched.duck.ReferencePosDuck;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MineshaftPieces.MineShaftCorridor.class)
public abstract class MineshaftCorridorMixin extends StructurePiece implements ReferencePosDuck {
	protected MineshaftCorridorMixin() {
		super(null, 0, null);
	}
	
	@WrapOperation(
		method = "postProcess",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean applyProcessorToSpawner(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation) {
		return NonTemplateProcessorHelper.getProcessorAndApply(this, this.boundingBox, level, pos, state, flags, operation);
	}
	
	@WrapOperation(
		method = {
			"fillPillarDownOrChainUp",
			"fillColumnDown"
		},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean applyProcessorToColumn(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, @Local(ordinal = 0) int x, @Local(ordinal = 1) int y, @Local(ordinal = 2) int z) {
		return NonTemplateProcessorHelper.getProcessorAndApply(this, this.boundingBox, level, pos, state, flags, operation, x, y, z);
	}
	
	@WrapOperation(
		method = "fillPillarDownOrChainUp",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/structures/MineshaftPieces$MineShaftCorridor;fillColumnBetween(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos$MutableBlockPos;II)V"
		)
	)
	private void applyProcessorToChains(WorldGenLevel level, BlockState pillarState, BlockPos.MutableBlockPos pos, int bottomInclusive, int topExclusive, Operation<Void> operation) {
		var processorList = NonTemplateProcessorHelper.getProcessor(this, level);
		if (processorList.isEmpty()) {
			operation.call(level, pillarState, pos, bottomInclusive, topExclusive);
			return;
		}
		
		for (int pillarY = bottomInclusive; pillarY < topExclusive; ++pillarY) {
			pos.setY(pillarY);
			BlockState processedState = NonTemplateProcessorHelper.processBlock(this, this.boundingBox, level, pos, pillarState, processorList.get());
			
			if (processedState != null) {
				level.setBlock(pos.setY(pillarY), processedState, 2);
			}
		}
	}
	
	@WrapOperation(
		method = "placeDoubleLowerOrUpperSupport",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
		)
	)
	private boolean useMineshaftExtensionsTag(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || state.is(LithostitchedBlockTags.SUPPORTS_MINESHAFT_EXTENSIONS);
	}
}
