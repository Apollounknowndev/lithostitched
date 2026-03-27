package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.ClocheHacks;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor2;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReplaceEffectsModifierActual {
	@Actual
	public void applyModifier(Biome biome) {
		BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
		BiomeAccessor2 accessor2 = (BiomeAccessor2) (Object) biome;
		BiomeSpecialEffects originalEffects = accessor.getSpecialEffects();
		ReplaceEffectsModifier $this = (ReplaceEffectsModifier) (Object) this;
		BiomeEffects modifierEffects = $this.effects();
		
		BiomeSpecialEffects.Builder effectBuilder = new BiomeSpecialEffects.Builder();
		$this.applyRequiredEffect(BiomeEffects::waterColor, originalEffects::waterColor, effectBuilder::waterColor);
		$this.applyOptionalEffect(BiomeEffects::foliageColor, originalEffects::foliageColorOverride, effectBuilder::foliageColorOverride);
		$this.applyOptionalEffect(BiomeEffects::dryFoliageColor, originalEffects::dryFoliageColorOverride, effectBuilder::dryFoliageColorOverride);
		$this.applyOptionalEffect(BiomeEffects::grassColor, originalEffects::grassColorOverride, effectBuilder::grassColorOverride);
		$this.applyRequiredEffect(BiomeEffects::grassColorModifier, originalEffects::grassColorModifier, effectBuilder::grassColorModifier);
		accessor.setSpecialEffects(effectBuilder.build());
		
		EnvironmentAttributeMap attributes = biome.getAttributes();
		var attributeBuilder = EnvironmentAttributeMap.builder();
		attributeBuilder.putAll(attributes);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::fogColor, EnvironmentAttributes.FOG_COLOR);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::waterFogColor, EnvironmentAttributes.WATER_FOG_COLOR);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::skyColor, EnvironmentAttributes.SKY_COLOR);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(e.ambientParticle().map(List::of).orElse(List.of())), EnvironmentAttributes.AMBIENT_PARTICLES);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(new AmbientSounds(e.ambientSound(), e.moodSound(), e.additionsSound().map(List::of).orElse(List.of()))), EnvironmentAttributes.AMBIENT_SOUNDS);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(new BackgroundMusic(e.music(), Optional.empty(), Optional.empty())), EnvironmentAttributes.BACKGROUND_MUSIC);
		ClocheHacks.applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::musicVolume, EnvironmentAttributes.MUSIC_VOLUME);
		accessor2.setAttributes(attributeBuilder.build());
	}
}
