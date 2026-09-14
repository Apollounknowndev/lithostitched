package dev.worldgen.lithostitched.api.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.internal.BiomeEffectsBuilderImpl;
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
