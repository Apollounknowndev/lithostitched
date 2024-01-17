package dev.worldgen.lithostitched.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;

public record BiomeEffects(Optional<Integer> fogColor, Optional<Integer> waterColor, Optional<Integer> waterFogColor, Optional<Integer> skyColor, Optional<Integer> foliageColorOverride, Optional<Integer> grassColorOverride, BiomeSpecialEffects.GrassColorModifier grassColorModifier, Optional<AmbientParticleSettings> ambientParticleSettings, Optional<Holder<SoundEvent>> ambientLoopSoundEvent, Optional<AmbientMoodSettings> ambientMoodSettings, Optional<AmbientAdditionsSettings> ambientAdditionsSettings, Optional<Music> backgroundMusic) {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("fog_color").forGetter(BiomeEffects::fogColor),
        Codec.INT.optionalFieldOf("water_color").forGetter(BiomeEffects::waterColor),
        Codec.INT.optionalFieldOf("water_fog_color").forGetter(BiomeEffects::waterFogColor),
        Codec.INT.optionalFieldOf("sky_color").forGetter(BiomeEffects::skyColor),
        Codec.INT.optionalFieldOf("foliage_color").forGetter(BiomeEffects::foliageColorOverride),
        Codec.INT.optionalFieldOf("grass_color").forGetter(BiomeEffects::grassColorOverride),
        BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter(BiomeEffects::grassColorModifier),
        AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter(BiomeEffects::ambientParticleSettings),
        SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(BiomeEffects::ambientLoopSoundEvent),
        AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter(BiomeEffects::ambientMoodSettings),
        AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter(BiomeEffects::ambientAdditionsSettings),
        Music.CODEC.optionalFieldOf("music").forGetter(BiomeEffects::backgroundMusic)
    ).apply(instance, BiomeEffects::new));
}