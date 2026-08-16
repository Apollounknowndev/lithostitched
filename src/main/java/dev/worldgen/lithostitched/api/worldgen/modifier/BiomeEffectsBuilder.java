package dev.worldgen.lithostitched.api.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.BiomeEffectsBuilderImpl;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

public interface BiomeEffectsBuilder {
	static BiomeEffectsBuilder create() {
		return new BiomeEffectsBuilderImpl();
	}
	
	BiomeEffectsBuilder fogColor(Integer fogColor);
	BiomeEffectsBuilder waterColor(Integer waterColor);
	BiomeEffectsBuilder waterFogColor(Integer waterFogColor);
	BiomeEffectsBuilder skyColor(Integer skyColor);
	BiomeEffectsBuilder foliageColor(Integer foliageColor);
	BiomeEffectsBuilder dryFoliageColor(Integer dryFoliageColor);
	BiomeEffectsBuilder grassColor(Integer grassColor);
	BiomeEffectsBuilder grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);
	BiomeEffectsBuilder ambientParticle(AmbientParticleSettings ambientParticle);
	BiomeEffectsBuilder ambientSound(Holder<SoundEvent> ambientSound);
	BiomeEffectsBuilder moodSound(AmbientMoodSettings moodSound);
	BiomeEffectsBuilder additionsSound(AmbientAdditionsSettings additionsSound);
	BiomeEffectsBuilder music(Music music);
	BiomeEffectsBuilder musicVolume(Float musicVolume);
	
	BiomeEffects build();
}
