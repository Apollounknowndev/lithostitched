package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlacedFeature.class)
public interface PlacedFeatureAccessor {
    @Accessor("feature")
    @Mutable
    void setFeature(Holder<ConfiguredFeature<?, ?>> feature);
}
