package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(Beardifier.class)
public abstract class BeardifierMixin {
    @WrapOperation(
        method = "forStructuresInChunk(Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/world/level/levelgen/Beardifier;",
        at = @At(
            value = "INVOKE",
            target = "add",
            ordinal = 0
        )
    )
    private static boolean overrideTerrainAdaptation(List<Beardifier.Rigid> list, Object rigid, Operation<Boolean> operation, @Local(ordinal = 0) StructurePiece structurePiece) {
        PoolElementStructurePiece piece = (PoolElementStructurePiece) structurePiece;

        if (piece.getElement() instanceof DelegatingPoolElement delegating) {
            Optional<TerrainAdjustment> terrainAdaptation = delegating.config().overrideTerrainAdaptation();
            if (terrainAdaptation.isPresent()) {
                return operation.call(list, new Beardifier.Rigid(piece.getBoundingBox(), terrainAdaptation.get(), piece.getGroundLevelDelta()));
            }
        }
        return operation.call(list, rigid);
    }
}