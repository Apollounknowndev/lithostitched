package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.duck.ReferencePosDuck;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MineshaftPieces.MineShaftPiece.class)
public abstract class MineshaftPieceMixin extends StructurePiece implements ReferencePosDuck {
	protected MineshaftPieceMixin() {
		super(null, 0, null);
	}
	
	@WrapOperation(
		method = "setPlanksBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean applyProcessor(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, @Local(ordinal = 0) int x, @Local(ordinal = 1) int y, @Local(ordinal = 2) int z) {
		return NonTemplateProcessorHelper.getProcessorAndApply(this, this.boundingBox, level, pos, state, flags, operation, x, y, z);
	}
}
