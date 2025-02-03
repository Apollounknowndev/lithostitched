package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.worldgen.processor.UnboundReferenceProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Inject(
        method = "placeInWorld",
        at = @At("HEAD")
    )
    private void bindUnboundProcessors(ServerLevelAccessor accessor, BlockPos pos1, BlockPos pos2, StructurePlaceSettings settings, RandomSource randomSource, int flags, CallbackInfoReturnable<Boolean> cir) {
        if (!settings.getProcessors().isEmpty()) {
            StructureProcessor lastProcessor = settings.getProcessors().getLast();
            if (lastProcessor instanceof UnboundReferenceProcessor unboundReference) {
                settings.popProcessor(unboundReference);
                settings.addProcessor(unboundReference.bind(accessor.getLevel()));
            }
        }
    }
}
