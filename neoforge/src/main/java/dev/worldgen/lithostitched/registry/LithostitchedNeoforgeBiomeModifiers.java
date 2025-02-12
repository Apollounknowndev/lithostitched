package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class LithostitchedNeoforgeBiomeModifiers {
    public record ReplaceClimateBiomeModifier(HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) implements BiomeModifier {
        public static final MapCodec<ReplaceClimateBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
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
        public MapCodec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }
    public record ReplaceEffectsBiomeModifier(HolderSet<Biome> biomes, BiomeEffects specialEffects) implements BiomeModifier {
        public static final MapCodec<ReplaceEffectsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsBiomeModifier::biomes),
            BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsBiomeModifier::specialEffects)
        ).apply(builder, ReplaceEffectsBiomeModifier::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder info) {
            if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
                BiomeSpecialEffectsBuilder builder = info.getSpecialEffects();
                tryApply(BiomeEffects::fogColor, builder::fogColor);
                tryApply(BiomeEffects::waterColor, builder::waterColor);
                tryApply(BiomeEffects::waterFogColor, builder::waterFogColor);
                tryApply(BiomeEffects::skyColor, builder::skyColor);

                tryApply(BiomeEffects::foliageColor, builder::foliageColorOverride);
                tryApply(BiomeEffects::grassColor, builder::grassColorOverride);
                tryApply(BiomeEffects::grassColorModifier, builder::grassColorModifier);

                tryApply(BiomeEffects::ambientParticle, builder::ambientParticle);
                tryApply(BiomeEffects::ambientSound, builder::ambientLoopSound);
                tryApply(BiomeEffects::moodSound, builder::ambientMoodSound);
                tryApply(BiomeEffects::additionsSound, builder::ambientAdditionsSound);
                tryApply(BiomeEffects::music, builder::backgroundMusic);
            }
        }

        private <T> void tryApply(Function<BiomeEffects, Optional<T>> getter, Consumer<T> applier) {
            getter.apply(this.specialEffects).ifPresent(applier);
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }
}
