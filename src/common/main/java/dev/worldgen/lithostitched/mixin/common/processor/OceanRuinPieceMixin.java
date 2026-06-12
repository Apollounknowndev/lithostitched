package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public class OceanRuinPieceMixin {
    @ModifyReturnValue(
        method = "makeSettings",
        at = @At("RETURN")
    )
    private static StructurePlaceSettings addShipwreckProcessor(StructurePlaceSettings settings, Rotation rotation, float f, OceanRuinStructure.Type type) {
        if (ConfigHandler.getConfig().breaksSeedParity()) {
            return settings.addProcessor(LithostitchedVersion.getUnboundReferenceProcessor("ocean_ruin_" + type.getName()));
        }
        return settings;
    }
}
