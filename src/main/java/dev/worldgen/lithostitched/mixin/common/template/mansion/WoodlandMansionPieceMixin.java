package dev.worldgen.lithostitched.mixin.common.template.mansion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WoodlandMansionPieces.WoodlandMansionPiece.class)
public class WoodlandMansionPieceMixin {
    @Inject(
        method = "makeLocation",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void convertStringToId(String string, CallbackInfoReturnable<ResourceLocation> cir) {
        if (string.contains(":")) {
            cir.setReturnValue(ResourceLocation.parse(string));
        }
    }
}
