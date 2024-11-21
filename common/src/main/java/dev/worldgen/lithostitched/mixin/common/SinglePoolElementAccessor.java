package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {
    @Accessor("template")
    @Mutable
    Either<ResourceLocation, StructureTemplate> getTemplate();

    @Accessor("processors")
    @Mutable
    Holder<StructureProcessorList> getProcessors();

    @Accessor("processors")
    @Mutable
    void setProcessors(Holder<StructureProcessorList> processors);
}
