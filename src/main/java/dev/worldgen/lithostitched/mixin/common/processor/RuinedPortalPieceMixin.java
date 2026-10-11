package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.api.tag.LithostitchedBlockTags;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.duck.ReferencePosDuck;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.internal.NonTemplateProcessorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(RuinedPortalPiece.class)
public abstract class RuinedPortalPieceMixin extends StructurePiece implements ReferencePosDuck {
    protected RuinedPortalPieceMixin() {
        super(null, 0, null);
    }
    
    @ModifyReturnValue(
        method = "makeSettings(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$VerticalPlacement;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$Properties;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;",
        at = @At("RETURN")
    )
    private static StructurePlaceSettings addRuinedPortalProcessor(StructurePlaceSettings settings) {
        return Lithostitched.breaksSeedParity() ? settings.addProcessor(UnboundReferenceProcessor.of("ruined_portal")) : settings;
    }
    
    @WrapOperation(
        method = {
            "maybeAddVines",
            "maybeAddLeavesAbove",
            "placeNetherrackOrMagma"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
        )
    )
    private boolean applyProcessorToExtraBits(LevelAccessor level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        Optional<StructureProcessorList> processors = NonTemplateProcessorHelper.getProcessor(this, level);
        if (processors.isEmpty()) {
            return original.call(level, pos, state);
        }
        
        BlockState processedState = NonTemplateProcessorHelper.processBlock(pos, level, pos, state, processors.get());
        if (processedState != null) {
            return original.call(level, pos, processedState);
        }
        return false;
    }
    
    @WrapOperation(
        method = "maybeAddLeavesAbove",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
        )
    )
    private boolean applyLeavesExtensionTag(BlockState state, Object object, Operation<Boolean> operation) {
        return operation.call(state, object) || state.is(LithostitchedBlockTags.SUPPORTS_RUINED_PORTAL_LEAVES);
    }
}
