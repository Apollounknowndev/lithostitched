package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.duck.ContextBiomeAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record BiomeCondition(HolderSet<Biome> biomes) implements SurfaceRules.ConditionSource {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LithostitchedCodecs.registrySet(Registries.BIOME, "biomes").forGetter(BiomeCondition::biomes)
    ).apply(instance, BiomeCondition::new));
    
    @Override
    public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }
    
    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        return ((ContextBiomeAccessor)(Object)context).lithostitched$biomeMatches(this.biomes);
    }
}
