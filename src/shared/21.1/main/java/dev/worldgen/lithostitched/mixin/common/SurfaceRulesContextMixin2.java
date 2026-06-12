package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.duck.ContextBiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(SurfaceRules.Context.class)
public class SurfaceRulesContextMixin2 implements ContextBiomeAccessor {
    @Shadow
    Supplier<Holder<Biome>> biome;
    
    @Override
    public SurfaceRules.Condition biomeMatches(HolderSet<Biome> biomes) {
        return () -> biomes.contains(biome.get());
    }
}