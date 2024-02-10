package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@Mixin(StructureTemplatePool.class)
public class StructureTemplatePoolMixin implements StructurePoolAccess {
    @Unique
    private ShufflingList<StructurePoolElement> lithostitchedTemplates = new ShufflingList<>();

    @Override
    public ShufflingList<StructurePoolElement> getLithostitchedTemplates() {
        return this.lithostitchedTemplates;
    }

    @Override
    public void setLithostitchedTemplates(ShufflingList<StructurePoolElement> templates) {
        this.lithostitchedTemplates = templates;
    }

    @Inject(
            method = "<init>(Lnet/minecraft/core/Holder;Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void lithostitched$addStructurePoolElementWeightedList(Holder<StructureTemplatePool> fallback, List<Pair<StructurePoolElement, Integer>> elementCounts, CallbackInfo ci) {
        elementCounts.forEach(pair -> lithostitchedTemplates.add(pair.getFirst(), pair.getSecond()));
    }
}