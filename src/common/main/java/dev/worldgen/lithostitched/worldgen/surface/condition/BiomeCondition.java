package dev.worldgen.lithostitched.worldgen.surface.condition;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record BiomeCondition(HolderSet<Biome> biomes) implements SurfaceRules.ConditionSource {
    public static final KeyDispatchDataCodec<BiomeCondition> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
        LithostitchedCodecs.registrySet(Registries.BIOME, "biomes").forGetter(BiomeCondition::biomes)
    ).apply(instance, BiomeCondition::new)));


    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    // Condition requires AW
    // Context requires AW
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        // I "love" functional interfaces for compilation
        // biome requires AW or mixin duck interface
        return () -> this.biomes.contains(((ContextAccessor)(Object)context).getBiome());
    }
}
