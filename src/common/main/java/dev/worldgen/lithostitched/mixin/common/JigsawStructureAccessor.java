package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor {
    @Accessor("poolAliases")
    @Mutable
    void setPoolAliases(List<PoolAliasBinding> poolAliases);

    @Accessor("poolAliases")
    List<PoolAliasBinding> getPoolAliases();
}
