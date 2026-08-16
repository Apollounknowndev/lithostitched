//? if neoforge {
package dev.worldgen.lithostitched.platform.neoforge.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddSpawnCostsModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.*;

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

    public record ReplaceEffectsBiomeModifier(HolderSet<Biome> biomes, BiomeEffects effects) implements BiomeModifier {
        public static final MapCodec<ReplaceEffectsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsBiomeModifier::biomes),
            BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsBiomeModifier::effects)
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
            getter.apply(this.effects).ifPresent(applier);
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec()
        {
            return CODEC;
        }
    }
    
    public record AddSpawnCostsBiomeModifier(AddSpawnCostsModifier modifier) implements BiomeModifier {
        public static final MapCodec<AddSpawnCostsBiomeModifier> CODEC = AddSpawnCostsModifier.CODEC.xmap(AddSpawnCostsBiomeModifier::new, AddSpawnCostsBiomeModifier::modifier);
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && this.modifier.biomes().contains(biome)) {
	            MobSpawnSettingsBuilder spawnSettings = builder.getMobSpawnSettings();
                for (var costEntry : this.modifier.spawnCosts().entrySet()) {
                    spawnSettings.addMobCharge(costEntry.getKey(), costEntry.getValue().charge(), costEntry.getValue().energyBudget());
                }
            }
        }
        
        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }
}
//? }