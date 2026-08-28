package dev.worldgen.lithostitched.impl.worldgen.modifier;

import java.util.Optional;

import dev.worldgen.lithostitched.api.worldgen.modifier.BiomeEffectsBuilder;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;

public class BiomeEffectsBuilderImpl implements BiomeEffectsBuilder {
	private Optional<Integer> waterColor = Optional.empty();
	private Optional<Integer> foliageColor = Optional.empty();
	private Optional<Integer> dryFoliageColor = Optional.empty();
	private Optional<Integer> grassColor = Optional.empty();
	private Optional<GrassColorModifier> grassColorModifier = Optional.empty();
	
	public BiomeEffectsBuilderImpl waterColor(Integer waterColor) {
		this.waterColor = Optional.ofNullable(waterColor);
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
	
	public BiomeEffects build() {
		return new BiomeEffects(
			waterColor,
			foliageColor,
			dryFoliageColor,
			grassColor,
			grassColorModifier
		);
	}
}