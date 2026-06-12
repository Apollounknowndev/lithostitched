package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
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
        return ConfigHandler.getConfig().breaksSeedParity() ? settings.addProcessor(LithostitchedVersion.getUnboundReferenceProcessor("igloo")) : settings;
    }
}
