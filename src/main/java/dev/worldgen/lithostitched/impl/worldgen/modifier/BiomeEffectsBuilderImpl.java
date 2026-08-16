package dev.worldgen.lithostitched.impl.worldgen.modifier;

import java.util.Optional;

import dev.worldgen.lithostitched.api.worldgen.modifier.BiomeEffectsBuilder;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;

public class BiomeEffectsBuilderImpl implements BiomeEffectsBuilder {
	private Optional<Integer> fogColor = Optional.empty();
	private Optional<Integer> waterColor = Optional.empty();
	private Optional<Integer> waterFogColor = Optional.empty();
	private Optional<Integer> skyColor = Optional.empty();
	private Optional<Integer> foliageColor = Optional.empty();
	private Optional<Integer> dryFoliageColor = Optional.empty();
	private Optional<Integer> grassColor = Optional.empty();
	private Optional<GrassColorModifier> grassColorModifier = Optional.empty();
	private Optional<AmbientParticleSettings> ambientParticle = Optional.empty();
	private Optional<Holder<SoundEvent>> ambientSound = Optional.empty();
	private Optional<AmbientMoodSettings> moodSound = Optional.empty();
	private Optional<AmbientAdditionsSettings> additionsSound = Optional.empty();
	private Optional<Music> music = Optional.empty();
	private Optional<Float> musicVolume = Optional.empty();
	
	public BiomeEffectsBuilderImpl fogColor(Integer fogColor) {
		this.fogColor = Optional.ofNullable(fogColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl waterColor(Integer waterColor) {
		this.waterColor = Optional.ofNullable(waterColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl waterFogColor(Integer waterFogColor) {
		this.waterFogColor = Optional.ofNullable(waterFogColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl skyColor(Integer skyColor) {
		this.skyColor = Optional.ofNullable(skyColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl foliageColor(Integer foliageColor) {
		this.foliageColor = Optional.ofNullable(foliageColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl dryFoliageColor(Integer dryFoliageColor) {
		this.dryFoliageColor = Optional.ofNullable(dryFoliageColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl grassColor(Integer grassColor) {
		this.grassColor = Optional.ofNullable(grassColor);
		return this;
	}
	
	public BiomeEffectsBuilderImpl grassColorModifier(GrassColorModifier grassColorModifier) {
		this.grassColorModifier = Optional.ofNullable(grassColorModifier);
		return this;
	}
	
	public BiomeEffectsBuilderImpl ambientParticle(AmbientParticleSettings ambientParticle) {
		this.ambientParticle = Optional.ofNullable(ambientParticle);
		return this;
	}
	
	public BiomeEffectsBuilderImpl ambientSound(Holder<SoundEvent> ambientSound) {
		this.ambientSound = Optional.ofNullable(ambientSound);
		return this;
	}
	
	public BiomeEffectsBuilderImpl moodSound(AmbientMoodSettings moodSound) {
		this.moodSound = Optional.ofNullable(moodSound);
		return this;
	}
	
	public BiomeEffectsBuilderImpl additionsSound(AmbientAdditionsSettings additionsSound) {
		this.additionsSound = Optional.ofNullable(additionsSound);
		return this;
	}
	
	public BiomeEffectsBuilderImpl music(Music music) {
		this.music = Optional.ofNullable(music);
		return this;
	}
	
	public BiomeEffectsBuilderImpl musicVolume(Float musicVolume) {
		this.musicVolume = Optional.ofNullable(musicVolume);
		return this;
	}
	
	public BiomeEffects build() {
		return new BiomeEffects(
			fogColor,
			waterColor,
			waterFogColor,
			skyColor,
			foliageColor,
			dryFoliageColor,
			grassColor,
			grassColorModifier,
			ambientParticle,
			ambientSound,
			moodSound,
			additionsSound,
			music,
			musicVolume
		);
	}
}