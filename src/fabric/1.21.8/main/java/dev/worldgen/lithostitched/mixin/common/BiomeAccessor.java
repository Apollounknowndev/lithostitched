package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeAccessor {

    @Accessor("climateSettings")
    @Mutable
    void setClimateSettings(Biome.ClimateSettings climateSettings);

    @Accessor("specialEffects")
    BiomeSpecialEffects getSpecialEffects();

    @Accessor("specialEffects")
    @Mutable
    void setSpecialEffects(BiomeSpecialEffects specialEffects);

    @Accessor("generationSettings")
    @Mutable
    void setGenerationSettings(BiomeGenerationSettings generationSettings);

    @Accessor("mobSettings")
    @Mutable
    void setMobSettings(MobSpawnSettings mobSettings);
}
