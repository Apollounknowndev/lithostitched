package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccessor {
    @Accessor("rawTemplates")
    @Mutable
    List<Pair<StructurePoolElement, Integer>> getRawTemplates();

    @Accessor("templates")
    @Mutable
    ObjectArrayList<StructurePoolElement> getTemplates();

    @Accessor("rawTemplates")
    @Mutable
    void setRawTemplates(List<Pair<StructurePoolElement, Integer>> elementCounts);

    @Accessor("templates")
    @Mutable
    void setTemplates(ObjectArrayList<StructurePoolElement> elements);
}
