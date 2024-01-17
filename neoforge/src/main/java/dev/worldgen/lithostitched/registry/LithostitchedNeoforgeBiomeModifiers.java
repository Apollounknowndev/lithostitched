package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.biome.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.*;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public class LithostitchedNeoforgeBiomeModifiers {
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
                if (specialEffects.skyColor().isPresent()) {
                    effects.skyColor(specialEffects.skyColor().get());
                }
                if (specialEffects.fogColor().isPresent()) {
                    effects.skyColor(specialEffects.fogColor().get());
                }
                if (specialEffects.waterColor().isPresent()) {
                    effects.skyColor(specialEffects.waterColor().get());
                }
                if (specialEffects.waterFogColor().isPresent()) {
                    effects.skyColor(specialEffects.waterFogColor().get());
                }
                if (specialEffects.grassColorOverride().isPresent()) {
                    effects.grassColorOverride(specialEffects.grassColorOverride().get());
                }
                if (specialEffects.foliageColorOverride().isPresent()) {
                    effects.grassColorOverride(specialEffects.foliageColorOverride().get());
                }
                effects.grassColorModifier(specialEffects.grassColorModifier());
                if (specialEffects.ambientLoopSoundEvent().isPresent()) {
                    effects.ambientLoopSound(specialEffects.ambientLoopSoundEvent().get());
                }
                if (specialEffects.ambientMoodSettings().isPresent()) {
                    effects.ambientMoodSound(specialEffects.ambientMoodSettings().get());
                }
                if (specialEffects.ambientAdditionsSettings().isPresent()) {
                    effects.ambientAdditionsSound(specialEffects.ambientAdditionsSettings().get());
                }
                if (specialEffects.backgroundMusic().isPresent()) {
                    effects.backgroundMusic(specialEffects.backgroundMusic().get());
                }
                if (specialEffects.ambientParticleSettings().isPresent()) {
                    effects.ambientParticle(specialEffects.ambientParticleSettings().get());
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
