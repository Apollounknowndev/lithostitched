package dev.worldgen.lithostitched.mixin.common.template.mansion;

import com.google.common.collect.Lists;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.duck.RegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(WoodlandMansionStructure.class)
public class WoodlandMansionStructureMixin {
    @Inject(
        method = "generatePieces",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectRegistriesForTemplateList(StructurePiecesBuilder builder, Structure.GenerationContext context, BlockPos pos, Rotation rotation, CallbackInfo ci) {
        if (ConfigHandler.getConfig().breaksSeedParity()) {
            LinkedList<WoodlandMansionPieces.WoodlandMansionPiece> list = Lists.newLinkedList();
            WoodlandMansionPieces.MansionGrid grid = new WoodlandMansionPieces.MansionGrid(context.random());
            WoodlandMansionPieces.MansionPiecePlacer placer = new WoodlandMansionPieces.MansionPiecePlacer(context.structureTemplateManager(), context.random());
            ((RegistryHolder)placer).setRegistries(context.registryAccess());
            placer.createMansion(pos, rotation, list, grid);
            list.forEach(builder::addPiece);
            ci.cancel();
        }
    }
}
