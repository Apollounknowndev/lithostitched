package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.worldgen.processor.UnboundReferenceProcessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShipwreckPieces.ShipwreckPiece.class)
public class ShipwreckPieceMixin {
    @Inject(
        method = "makeSettings(Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;",
        at = @At("RETURN")
    )
    private static void addShipwreckProcessor(Rotation rot, CallbackInfoReturnable<StructurePlaceSettings> cir) {
        if (ConfigHandler.getConfig().breaksSeedParity()) {
            cir.getReturnValue().addProcessor(new UnboundReferenceProcessor(LithostitchedCommon.id("shipwreck")));
        }
    }
}
