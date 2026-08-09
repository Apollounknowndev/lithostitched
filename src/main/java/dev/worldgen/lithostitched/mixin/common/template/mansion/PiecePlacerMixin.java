package dev.worldgen.lithostitched.mixin.common.template.mansion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.impl.duck.RegistryHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(
        method = "createMansion",
        at = @At(
            value = "NEW",
            target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$FirstFloorRoomCollection"
        )
    )
    private WoodlandMansionPieces.FirstFloorRoomCollection addFirstFloorRegistries(Operation<WoodlandMansionPieces.FirstFloorRoomCollection> operation) {
        var collection = operation.call();
        ((RegistryHolder)collection).setRegistries(this.registries);
        return collection;
    }

    @WrapOperation(
        method = "createMansion",
        at = @At(
            value = "NEW",
            target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$SecondFloorRoomCollection"
        )
    )
    private WoodlandMansionPieces.SecondFloorRoomCollection addSecondFloorRegistries(Operation<WoodlandMansionPieces.SecondFloorRoomCollection> operation) {
        var collection = operation.call();
        ((RegistryHolder)collection).setRegistries(this.registries);
        return collection;
    }

    @WrapOperation(
        method = "createMansion",
        at = @At(
            value = "NEW",
            target = "net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$ThirdFloorRoomCollection"
        )
    )
    private WoodlandMansionPieces.ThirdFloorRoomCollection addThirdFloorRegistries(Operation<WoodlandMansionPieces.ThirdFloorRoomCollection> operation) {
        var collection = operation.call();
        ((RegistryHolder)collection).setRegistries(this.registries);
        return collection;
    }
}
