package dev.worldgen.lithostitched.api.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.BiomeEffectsBuilderImpl;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

public interface BiomeEffectsBuilder {
	static BiomeEffectsBuilder create() {
		return new BiomeEffectsBuilderImpl();
	}
	
	BiomeEffectsBuilder waterColor(Integer waterColor);
	BiomeEffectsBuilder foliageColor(Integer foliageColor);
	BiomeEffectsBuilder dryFoliageColor(Integer dryFoliageColor);
	BiomeEffectsBuilder grassColor(Integer grassColor);
	BiomeEffectsBuilder grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);
	
	BiomeEffects build();
}
