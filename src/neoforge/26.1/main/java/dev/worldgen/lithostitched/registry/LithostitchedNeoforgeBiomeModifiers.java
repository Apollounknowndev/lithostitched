package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor2;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class LithostitchedNeoforgeBiomeModifiers {
    public record ReplaceClimateBiomeModifier(HolderSet<Biome> biomes, BiomeClimate climateSettings) implements BiomeModifier {
        public static final MapCodec<ReplaceClimateBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateBiomeModifier::biomes),
            BiomeClimate.CODEC.fieldOf("climate").forGetter(ReplaceClimateBiomeModifier::climateSettings)
        ).apply(builder, ReplaceClimateBiomeModifier::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
                ClimateSettingsBuilder climateSettings = builder.getClimateSettings();
                tryApply(this.climateSettings.temperature(), climateSettings::setTemperature);
                tryApply(this.climateSettings.temperatureModifier(), climateSettings::setTemperatureModifier);
                tryApply(this.climateSettings.hasPrecipitation(), climateSettings::setHasPrecipitation);
                tryApply(this.climateSettings.downfall(), climateSettings::setDownfall);
            }
        }

        private <T> void tryApply(Optional<T> value, Consumer<T> consumer) {
            value.ifPresent(consumer::accept);
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
                applyEffect(BiomeEffects::waterColor, builder::waterColor);
                applyEffect(BiomeEffects::foliageColor, builder::foliageColorOverride);
                applyEffect(BiomeEffects::dryFoliageColor, builder::dryFoliageColorOverride);
                applyEffect(BiomeEffects::grassColor, builder::grassColorOverride);
                applyEffect(BiomeEffects::grassColorModifier, builder::grassColorModifier);

                BiomeAccessor2 accessor = (BiomeAccessor2) (Object) biome.value();
                EnvironmentAttributeMap attributes = biome.value().getAttributes();
                var attributeBuilder = EnvironmentAttributeMap.builder();
                attributeBuilder.putAll(attributes);
                applyAttribute(attributeBuilder, BiomeEffects::fogColor, EnvironmentAttributes.FOG_COLOR);
                applyAttribute(attributeBuilder, BiomeEffects::waterFogColor, EnvironmentAttributes.WATER_FOG_COLOR);
                applyAttribute(attributeBuilder, BiomeEffects::skyColor, EnvironmentAttributes.SKY_COLOR);
                applyAttribute(attributeBuilder, e -> Optional.of(e.ambientParticle().map(List::of).orElse(List.of())), EnvironmentAttributes.AMBIENT_PARTICLES);
                applyAttribute(attributeBuilder, e -> Optional.of(new AmbientSounds(e.ambientSound(), e.moodSound(), e.additionsSound().map(List::of).orElse(List.of()))), EnvironmentAttributes.AMBIENT_SOUNDS);
                applyAttribute(attributeBuilder, e -> Optional.of(new BackgroundMusic(e.music(), Optional.empty(), Optional.empty())), EnvironmentAttributes.BACKGROUND_MUSIC);
                applyAttribute(attributeBuilder, BiomeEffects::musicVolume, EnvironmentAttributes.MUSIC_VOLUME);
                accessor.setAttributes(attributeBuilder.build());
            }
        }

        private <T> void applyEffect(Function<BiomeEffects, Optional<T>> getter, Consumer<T> applier) {
            getter.apply(this.specialEffects).ifPresent(applier);
        }

        private <T> void applyAttribute(EnvironmentAttributeMap.Builder builder, Function<BiomeEffects, Optional<T>> getter, EnvironmentAttribute<T> attribute) {
            Optional<T> value = getter.apply(this.specialEffects);
            value.ifPresent(object -> builder.set(attribute, object));
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }
}
