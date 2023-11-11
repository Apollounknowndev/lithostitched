package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.biome.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import net.minecraftforge.common.world.ClimateSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.Optional;

public class LithostitchedForgeBiomeModifiers {
    public record ReplaceClimateBiomeModifier(HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) implements BiomeModifier {
        public static final Codec<ReplaceClimateBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateBiomeModifier::biomes),
            Biome.ClimateSettings.CODEC.fieldOf("climate").forGetter(ReplaceClimateBiomeModifier::climateSettings)
        ).apply(builder, ReplaceClimateBiomeModifier::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
                ClimateSettingsBuilder climateSettings = builder.getClimateSettings();
                climateSettings.setTemperature(this.climateSettings().temperature());
                climateSettings.setDownfall(this.climateSettings().downfall());
                climateSettings.setHasPrecipitation(this.climateSettings().hasPrecipitation());
                climateSettings.setTemperatureModifier(this.climateSettings().temperatureModifier());
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }
    public record ReplaceEffectsBiomeModifier(HolderSet<Biome> biomes, BiomeEffects specialEffects) implements BiomeModifier {
        public static final Codec<ReplaceEffectsBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsBiomeModifier::biomes),
            BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsBiomeModifier::specialEffects)
        ).apply(builder, ReplaceEffectsBiomeModifier::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
                BiomeSpecialEffectsBuilder effects = builder.getSpecialEffects();
                if (specialEffects.getSkyColor().isPresent()) {
                    effects.skyColor(specialEffects.getSkyColor().get());
                }
                if (specialEffects.getFogColor().isPresent()) {
                    effects.skyColor(specialEffects.getFogColor().get());
                }
                if (specialEffects.getWaterColor().isPresent()) {
                    effects.skyColor(specialEffects.getWaterColor().get());
                }
                if (specialEffects.getWaterFogColor().isPresent()) {
                    effects.skyColor(specialEffects.getWaterFogColor().get());
                }
                if (specialEffects.getGrassColorOverride().isPresent()) {
                    effects.grassColorOverride(specialEffects.getGrassColorOverride().get());
                }
                if (specialEffects.getFoliageColorOverride().isPresent()) {
                    effects.grassColorOverride(specialEffects.getFoliageColorOverride().get());
                }
                effects.grassColorModifier(specialEffects.getGrassColorModifier());
                if (specialEffects.getAmbientLoopSoundEvent().isPresent()) {
                    effects.ambientLoopSound(specialEffects.getAmbientLoopSoundEvent().get());
                }
                if (specialEffects.getAmbientMoodSettings().isPresent()) {
                    effects.ambientMoodSound(specialEffects.getAmbientMoodSettings().get());
                }
                if (specialEffects.getAmbientAdditionsSettings().isPresent()) {
                    effects.ambientAdditionsSound(specialEffects.getAmbientAdditionsSettings().get());
                }
                if (specialEffects.getBackgroundMusic().isPresent()) {
                    effects.backgroundMusic(specialEffects.getBackgroundMusic().get());
                }
                if (specialEffects.getAmbientParticleSettings().isPresent()) {
                    effects.ambientParticle(specialEffects.getAmbientParticleSettings().get());
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }


}
