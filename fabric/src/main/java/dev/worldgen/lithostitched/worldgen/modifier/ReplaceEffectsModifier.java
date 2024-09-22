package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.biome.BiomeSpecialEffects.Builder;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Modifier} implementation that replaces the biome special effects of {@link Biome} entries.
 *
 * @author Apollo
 */
public record ReplaceEffectsModifier(HolderSet<Biome> biomes, ModdedBiomeEffects effects) implements Modifier {

    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        ModdedBiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
    ).apply(instance, ReplaceEffectsModifier::new));

    @SuppressWarnings("unchecked")
    @Override
    public void applyModifier(RegistryAccess registryAccess) {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        Registry<Biome> registry = registryAccess.registryOrThrow(Registries.BIOME);
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());

            if (entry.unwrapKey().isPresent()) {
                ResourceKey<Biome> key = entry.unwrapKey().get();
                Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
                knownPackInfo.ifPresent(registrationInfo -> ((MappedRegistryAccessor<Biome>)registry).lithostitched$getRegistrationInfos().put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle())));
            }
        }
    }

    @Override
    public void applyModifier() {}

    public void applyModifier(Biome biome) {
        //TODO: Make this code not terrible
        BiomeSpecialEffects originalEffects = ((BiomeAccessor)(Object)biome).getSpecialEffects();
        Builder mergedEffectsBuilder = new Builder()
            .skyColor(this.effects().skyColor().orElse(originalEffects.getSkyColor()))
            .fogColor(this.effects().fogColor().orElse(originalEffects.getFogColor()))
            .waterColor(this.effects().waterColor().orElse(originalEffects.getWaterColor()))
            .waterFogColor(this.effects().waterFogColor().orElse(originalEffects.getWaterFogColor()))
            .grassColorModifier(this.effects().grassColorModifier().orElse(originalEffects.getGrassColorModifier()))
            .backgroundMusic(this.effects().backgroundMusic().orElse(originalEffects.getBackgroundMusic().orElse(null)));
        Integer grassColorOverride = this.effects.grassColorOverride().orElse(originalEffects.getGrassColorOverride().orElse(null));
        if (grassColorOverride != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.grassColorOverride(grassColorOverride);
        }
        Integer foliageColorOverride = this.effects.foliageColorOverride().orElse(originalEffects.getFoliageColorOverride().orElse(null));
        if (foliageColorOverride != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.foliageColorOverride(foliageColorOverride);
        }
        AmbientParticleSettings ambientParticleSettings = this.effects.ambientParticleSettings().orElse(originalEffects.getAmbientParticleSettings().orElse(null));
        if (ambientParticleSettings != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.ambientParticle(ambientParticleSettings);
        }
        Holder<SoundEvent> ambientLoopSound = this.effects.ambientLoopSoundEvent().orElse(originalEffects.getAmbientLoopSoundEvent().orElse(null));
        if (ambientLoopSound != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.ambientLoopSound(ambientLoopSound);
        }
        AmbientMoodSettings ambientMoodSettings = this.effects.ambientMoodSettings().orElse(originalEffects.getAmbientMoodSettings().orElse(null));
        if (ambientMoodSettings != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.ambientMoodSound(ambientMoodSettings);
        }
        AmbientAdditionsSettings ambientAdditionsSettings = this.effects.ambientAdditionsSettings().orElse(originalEffects.getAmbientAdditionsSettings().orElse(null));
        if (ambientAdditionsSettings != null) {
            mergedEffectsBuilder = mergedEffectsBuilder.ambientAdditionsSound(ambientAdditionsSettings);
        }

        ((BiomeAccessor) (Object) biome).setSpecialEffects(mergedEffectsBuilder.build());
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }

    /**
     * Alternative to {@link BiomeSpecialEffects} which has all fields optional.
     *
     * @author Apollo
     */
    public record ModdedBiomeEffects(Optional<Integer> skyColor, Optional<Integer> fogColor, Optional<Integer> waterColor, Optional<Integer> waterFogColor, Optional<Integer> foliageColorOverride, Optional<Integer> grassColorOverride, Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier, Optional<AmbientParticleSettings> ambientParticleSettings, Optional<Holder<SoundEvent>> ambientLoopSoundEvent, Optional<AmbientMoodSettings> ambientMoodSettings, Optional<AmbientAdditionsSettings> ambientAdditionsSettings, Optional<Music> backgroundMusic) {
        public static final Codec<ModdedBiomeEffects> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("sky_color").forGetter(ModdedBiomeEffects::skyColor),
            Codec.INT.optionalFieldOf("fog_color").forGetter(ModdedBiomeEffects::fogColor),
            Codec.INT.optionalFieldOf("water_color").forGetter(ModdedBiomeEffects::waterColor),
            Codec.INT.optionalFieldOf("water_fog_color").forGetter(ModdedBiomeEffects::waterFogColor),
            Codec.INT.optionalFieldOf("foliage_color").forGetter(ModdedBiomeEffects::foliageColorOverride),
            Codec.INT.optionalFieldOf("grass_color").forGetter(ModdedBiomeEffects::grassColorOverride),
            BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier").forGetter(ModdedBiomeEffects::grassColorModifier),
            AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter(ModdedBiomeEffects::ambientParticleSettings),
            SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(ModdedBiomeEffects::ambientLoopSoundEvent),
            AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter(ModdedBiomeEffects::ambientMoodSettings),
            AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter(ModdedBiomeEffects::ambientAdditionsSettings),
            Music.CODEC.optionalFieldOf("music").forGetter(ModdedBiomeEffects::backgroundMusic)
        ).apply(instance, ModdedBiomeEffects::new));
    }

}

