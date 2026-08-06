package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor("attributes")
    @Mutable
    void setAttributes(EnvironmentAttributeMap map);
}
