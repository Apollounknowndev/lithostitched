package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.impl.duck.ReferencePosDuck;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StrongholdPieces.PortalRoom.class)
public abstract class PortalRoomMixin extends StructurePiece implements ReferencePosDuck {
    protected PortalRoomMixin() {
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
}
