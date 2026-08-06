package dev.worldgen.lithostitched.mixin.common.template.mansion;

import dev.worldgen.lithostitched.duck.RegistryHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WoodlandMansionPieces.FloorRoomCollection.class)
public abstract class FloorRoomCollectionMixin implements RegistryHolder {
    private RegistryAccess registries;

    @Override
    public RegistryAccess getRegistries() {
        return this.registries;
    }

    @Override
    public void setRegistries(RegistryAccess registries) {
        this.registries = registries;
    }
}
