package dev.worldgen.lithostitched.worldgen.modifier.util;

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

public record BiomeEffects(Optional<Integer> fogColor, Optional<Integer> waterColor, Optional<Integer> waterFogColor, Optional<Integer> skyColor, Optional<Integer> foliageColor, Optional<Integer> grassColor, Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier, Optional<AmbientParticleSettings> ambientParticle, Optional<Holder<SoundEvent>> ambientSound, Optional<AmbientMoodSettings> moodSound, Optional<AmbientAdditionsSettings> additionsSound, Optional<Music> music) {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("fog_color").forGetter(BiomeEffects::fogColor),
        Codec.INT.optionalFieldOf("water_color").forGetter(BiomeEffects::waterColor),
        Codec.INT.optionalFieldOf("water_fog_color").forGetter(BiomeEffects::waterFogColor),
        Codec.INT.optionalFieldOf("sky_color").forGetter(BiomeEffects::skyColor),
        Codec.INT.optionalFieldOf("foliage_color").forGetter(BiomeEffects::foliageColor),
        Codec.INT.optionalFieldOf("grass_color").forGetter(BiomeEffects::grassColor),
        BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier").forGetter(BiomeEffects::grassColorModifier),
        AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter(BiomeEffects::ambientParticle),
        SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(BiomeEffects::ambientSound),
        AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter(BiomeEffects::moodSound),
        AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter(BiomeEffects::additionsSound),
        Music.CODEC.optionalFieldOf("music").forGetter(BiomeEffects::music)
    ).apply(instance, BiomeEffects::new));
}