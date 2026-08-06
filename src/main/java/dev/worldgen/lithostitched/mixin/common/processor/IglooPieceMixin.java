package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IglooPieces.IglooPiece.class)
public class IglooPieceMixin {
    @ModifyReturnValue(
        method = "makeSettings",
        at = @At("RETURN")
    )
    private static StructurePlaceSettings addShipwreckProcessor(StructurePlaceSettings settings) {
        return Lithostitched.breaksSeedParity() ? settings.addProcessor(UnboundReferenceProcessor.of("igloo")) : settings;
    }
}
