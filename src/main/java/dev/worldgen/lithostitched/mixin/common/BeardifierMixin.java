package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import it.unimi.dsi.fastutil.objects.ObjectList;
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
        //? if fabric
        //method = "method_42694",
        //? if neoforge
        method = "lambda$forStructuresInChunk$2(Lnet/minecraft/world/level/ChunkPos;Lit/unimi/dsi/fastutil/objects/ObjectList;IILit/unimi/dsi/fastutil/objects/ObjectList;Lnet/minecraft/world/level/levelgen/structure/StructureStart;)V",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectList;add(Ljava/lang/Object;)Z",
            //? if fabric
            //ordinal = 0
            //? if neoforge
            ordinal = 1
        )
    )
    private static boolean overrideTerrainAdaptation(ObjectList<Beardifier.Rigid> list, Object rigid, Operation<Boolean> operation, @Local(ordinal = 0) StructurePiece structurePiece) {
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