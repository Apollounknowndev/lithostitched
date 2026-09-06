package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.duck.ContextBiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(SurfaceRules.Context.class)
public abstract class SurfaceRulesContextMixin2 implements ContextBiomeAccessor {
    @Shadow
    @Final
    private Set<Holder<Biome>> possibleBiomes;
    
    @Shadow
    protected abstract Holder<Biome> getBiome();
    
    @Override
    public SurfaceRules.Condition lithostitched$biomeMatches(HolderSet<Biome> biomes) {
        if (this.possibleBiomes != null) {
            if (ContextBiomeAccessor.canNeverMatch(biomes, this.possibleBiomes)) {
                return () -> false;
            }
            
            if (ContextBiomeAccessor.willAlwaysMatch(biomes, this.possibleBiomes)) {
                return () -> true;
            }
        }
        
        return () -> biomes.contains(this.getBiome());
    }
}