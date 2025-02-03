package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.world.level.levelgen.Beardifier;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Optional;

@Mixin(value = Beardifier.class, remap = false)
public abstract class BeardifierMixin {
    @Inject(
        method = "method_42694",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectList;add(Ljava/lang/Object;)Z",
            shift = At.Shift.AFTER,
            ordinal = 1
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void overrideTerrainAdaption(
        ChunkPos chunkPos, ObjectList<Beardifier.Rigid> rigids, int i, int j, ObjectList jigsawJunctions, StructureStart structureStart, CallbackInfo ci,
        TerrainAdjustment terrainAdjustment, Iterator var7, StructurePiece structurePiece, PoolElementStructurePiece piece, StructureTemplatePool.Projection projection
    ) {
        if (piece.getElement() instanceof DelegatingPoolElement delegating) {
            Optional<TerrainAdjustment> terrainAdaption = delegating.config().overrideTerrainAdaption();
            if (terrainAdaption.isPresent()) {
                rigids.remove(rigids.size() - 1);
                rigids.add(new Beardifier.Rigid(piece.getBoundingBox(), terrainAdaption.get(), piece.getGroundLevelDelta()));
            }
        }
    }
}