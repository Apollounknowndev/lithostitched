package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.msrandom.multiplatform.annotations.Actual;

public class ReplaceEffectsModifierActual {
	@Actual
	public void applyModifier(Biome biome) {
		BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
		BiomeSpecialEffects originalEffects = accessor.getSpecialEffects();
		BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
		ReplaceEffectsModifier $this = (ReplaceEffectsModifier) (Object) this;
		
		$this.applyRequiredEffect(BiomeEffects::fogColor, originalEffects::getFogColor, builder::fogColor);
		$this.applyRequiredEffect(BiomeEffects::waterColor, originalEffects::getWaterColor, builder::waterColor);
		$this.applyRequiredEffect(BiomeEffects::waterFogColor, originalEffects::getWaterFogColor, builder::waterFogColor);
		$this.applyRequiredEffect(BiomeEffects::skyColor, originalEffects::getSkyColor, builder::skyColor);
		
		$this.applyOptionalEffect(BiomeEffects::foliageColor, originalEffects::getFoliageColorOverride, builder::foliageColorOverride);
		$this.applyOptionalEffect(BiomeEffects::grassColor, originalEffects::getGrassColorOverride, builder::grassColorOverride);
		$this.applyRequiredEffect(BiomeEffects::grassColorModifier, originalEffects::getGrassColorModifier, builder::grassColorModifier);
		
		$this.applyOptionalEffect(BiomeEffects::ambientParticle, originalEffects::getAmbientParticleSettings, builder::ambientParticle);
		$this.applyOptionalEffect(BiomeEffects::ambientSound, originalEffects::getAmbientLoopSoundEvent, builder::ambientLoopSound);
		$this.applyOptionalEffect(BiomeEffects::moodSound, originalEffects::getAmbientMoodSettings, builder::ambientMoodSound);
		$this.applyOptionalEffect(BiomeEffects::additionsSound, originalEffects::getAmbientAdditionsSettings, builder::ambientAdditionsSound);
		$this.applyOptionalEffect(BiomeEffects::music, originalEffects::getBackgroundMusic, builder::backgroundMusic);
		
		accessor.setSpecialEffects(builder.build());
	}
}
