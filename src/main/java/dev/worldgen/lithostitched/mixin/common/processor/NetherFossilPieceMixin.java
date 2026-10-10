package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.worldgen.processor.LithostitchedProcessorLists;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherFossilPieces.NetherFossilPiece.class)
public class NetherFossilPieceMixin {
    @ModifyReturnValue(
        method = "makeSettings",
        at = @At("RETURN")
    )
    private static StructurePlaceSettings addShipwreckProcessor(StructurePlaceSettings settings) {
        return Lithostitched.breaksSeedParity() ? settings.addProcessor(UnboundReferenceProcessor.of("nether_fossil")) : settings;
    }
    
    @WrapOperation(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        ),
        method = "placeDriedGhast"
    )
    private static boolean applyProcessorToDriedGhast(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, @Local(ordinal = 0, argsOnly = true) BoundingBox fossilBB) {
        var processorList = NonTemplateProcessorHelper.getProcessor(level, LithostitchedProcessorLists.NETHER_FOSSIL);
        if (processorList.isEmpty()) {
            return operation.call(level, pos, state, flags);
        }
        
        BlockState processedState = NonTemplateProcessorHelper.processBlock(new BlockPos(fossilBB.minX(), fossilBB.minY(), fossilBB.minZ()), level, pos, state, processorList.get());
        if (processedState != null) {
            return operation.call(level, pos, processedState, flags);
        }
        return false;
    }
}
