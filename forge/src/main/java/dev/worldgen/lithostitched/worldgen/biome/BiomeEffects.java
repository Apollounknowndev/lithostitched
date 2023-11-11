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

public class BiomeEffects {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create((p_47971_) -> {
        return p_47971_.group(Codec.INT.optionalFieldOf("fog_color").forGetter((p_151782_) -> {
            return p_151782_.fogColor;
        }), Codec.INT.optionalFieldOf("water_color").forGetter((p_151780_) -> {
            return p_151780_.waterColor;
        }), Codec.INT.optionalFieldOf("water_fog_color").forGetter((p_151778_) -> {
            return p_151778_.waterFogColor;
        }), Codec.INT.optionalFieldOf("sky_color").forGetter((p_151776_) -> {
            return p_151776_.skyColor;
        }), Codec.INT.optionalFieldOf("foliage_color").forGetter((p_151774_) -> {
            return p_151774_.foliageColorOverride;
        }), Codec.INT.optionalFieldOf("grass_color").forGetter((p_151772_) -> {
            return p_151772_.grassColorOverride;
        }), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter((p_151770_) -> {
            return p_151770_.grassColorModifier;
        }), AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter((p_151768_) -> {
            return p_151768_.ambientParticleSettings;
        }), SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter((p_151766_) -> {
            return p_151766_.ambientLoopSoundEvent;
        }), AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter((p_151764_) -> {
            return p_151764_.ambientMoodSettings;
        }), AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter((p_151762_) -> {
            return p_151762_.ambientAdditionsSettings;
        }), Music.CODEC.optionalFieldOf("music").forGetter((p_151760_) -> {
            return p_151760_.backgroundMusic;
        })).apply(p_47971_, BiomeEffects::new);
    });
    private final Optional<Integer> fogColor;
    private final Optional<Integer> waterColor;
    private final Optional<Integer> waterFogColor;
    private final Optional<Integer> skyColor;
    private final Optional<Integer> foliageColorOverride;
    private final Optional<Integer> grassColorOverride;
    private final BiomeSpecialEffects.GrassColorModifier grassColorModifier;
    private final Optional<AmbientParticleSettings> ambientParticleSettings;
    private final Optional<Holder<SoundEvent>> ambientLoopSoundEvent;
    private final Optional<AmbientMoodSettings> ambientMoodSettings;
    private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
    private final Optional<Music> backgroundMusic;

    BiomeEffects(Optional<Integer> p_47941_, Optional<Integer> p_47942_, Optional<Integer> p_47943_, Optional<Integer> p_47944_, Optional<Integer> p_47945_, Optional<Integer> p_47946_, BiomeSpecialEffects.GrassColorModifier p_47947_, Optional<AmbientParticleSettings> p_47948_, Optional<Holder<SoundEvent>> p_47949_, Optional<AmbientMoodSettings> p_47950_, Optional<AmbientAdditionsSettings> p_47951_, Optional<Music> p_47952_) {
        this.fogColor = p_47941_;
        this.waterColor = p_47942_;
        this.waterFogColor = p_47943_;
        this.skyColor = p_47944_;
        this.foliageColorOverride = p_47945_;
        this.grassColorOverride = p_47946_;
        this.grassColorModifier = p_47947_;
        this.ambientParticleSettings = p_47948_;
        this.ambientLoopSoundEvent = p_47949_;
        this.ambientMoodSettings = p_47950_;
        this.ambientAdditionsSettings = p_47951_;
        this.backgroundMusic = p_47952_;
    }

    public Optional<Integer> getFogColor() {
        return this.fogColor;
    }

    public Optional<Integer> getWaterColor() {
        return this.waterColor;
    }

    public Optional<Integer> getWaterFogColor() {
        return this.waterFogColor;
    }

    public Optional<Integer> getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColorOverride() {
        return this.foliageColorOverride;
    }

    public Optional<Integer> getGrassColorOverride() {
        return this.grassColorOverride;
    }

    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.ambientParticleSettings;
    }

    public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
        return this.ambientLoopSoundEvent;
    }

    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.ambientMoodSettings;
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.ambientAdditionsSettings;
    }

    public Optional<Music> getBackgroundMusic() {
        return this.backgroundMusic;
    }
}