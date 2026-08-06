package dev.worldgen.lithostitched.mixin.common.template;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilPieces;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(NetherFossilStructure.class)
public class NetherFossilStructureMixin {
    @WrapOperation(
        method = "findGenerationPoint",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"
        )
    )
    private Optional<Structure.GenerationStub> useTemplateList(Object stub, Operation<Optional<Structure.GenerationStub>> operation, Structure.GenerationContext context, @Local(ordinal = 0) BlockPos pos) {
        if (!Lithostitched.breaksSeedParity()) {
            return operation.call(stub);
        }
        return Optional.of(
            new Structure.GenerationStub(
                pos,
                builder -> {
                    Rotation rotation = Rotation.getRandom(context.random());
                    Identifier template = TemplateLists.getRandom(context.registryAccess(), TemplateLists.NETHER_FOSSIL, context.random());
                    builder.addPiece(new NetherFossilPieces.NetherFossilPiece(context.structureTemplateManager(), template, pos, rotation));
                }
            )
        );
    }
}
