package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.duck.MaterialSystemAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.MaterialSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MaterialRuleContext.class)
public class MaterialRuleContextMixin implements MaterialSystemAccessor {
    @Shadow @Final MaterialSystem system;
    
    @Override
    public MaterialSystem getSystem() {
        return this.system;
    }
}