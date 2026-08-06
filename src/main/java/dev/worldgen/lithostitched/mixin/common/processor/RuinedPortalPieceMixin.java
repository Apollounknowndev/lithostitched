package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RuinedPortalPiece.class)
public class RuinedPortalPieceMixin {
    @ModifyReturnValue(
        method = "makeSettings(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$VerticalPlacement;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalPiece$Properties;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;",
        at = @At("RETURN")
    )
    private static StructurePlaceSettings addRuinedPortalProcessor(StructurePlaceSettings settings) {
        return Lithostitched.breaksSeedParity() ? settings.addProcessor(UnboundReferenceProcessor.of("ruined_portal")) : settings;
    }
}
