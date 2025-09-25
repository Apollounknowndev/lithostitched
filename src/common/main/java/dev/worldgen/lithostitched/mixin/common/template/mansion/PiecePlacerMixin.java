package dev.worldgen.lithostitched.mixin.common.template.mansion;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.duck.RegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WoodlandMansionPieces.MansionPiecePlacer.class)
public class PiecePlacerMixin implements RegistryHolder {
    private RegistryAccess registries;

    @Override
    public RegistryAccess getRegistries() {
        return this.registries;
    }

    @Override
    public void setRegistries(RegistryAccess registries) {
        this.registries = registries;
    }

    @Definition(id = "floorRoomCollections", local = @Local(type = WoodlandMansionPieces.FloorRoomCollection[].class))
    @Expression("floorRoomCollections[2] = ?")
    @Inject(
        method = "createMansion",
        at = @At(
            value = "MIXINEXTRAS:EXPRESSION",
            shift = At.Shift.AFTER
        )
    )
    private void addRegistries(BlockPos pos, Rotation rotation, List<WoodlandMansionPieces.WoodlandMansionPiece> list,
       WoodlandMansionPieces.MansionGrid grid, CallbackInfo ci, @Local(ordinal = 0) WoodlandMansionPieces.FloorRoomCollection[] collection) {
        ((RegistryHolder)collection[0]).setRegistries(this.registries);
        ((RegistryHolder)collection[1]).setRegistries(this.registries);
        ((RegistryHolder)collection[2]).setRegistries(this.registries);
    }
}
