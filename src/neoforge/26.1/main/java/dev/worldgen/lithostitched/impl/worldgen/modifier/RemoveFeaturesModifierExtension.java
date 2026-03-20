package dev.worldgen.lithostitched.impl.worldgen.modifier;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.Collections;

@ClassExtension(RemoveFeaturesModifier.class)
public class RemoveFeaturesModifierExtension implements NeoforgeModifierHolder {
	@ExtensionShadow
	private final HolderSet<Biome> biomes;
	@ExtensionShadow
	private final HolderSet<PlacedFeature> features;
	@ExtensionShadow
	private final GenerationStep.Decoration step;
	
	@ExtensionInject
	@Override
	public BiomeModifier createNeoforgeModifier() {
		return new BiomeModifiers.RemoveFeaturesBiomeModifier(biomes, features, Collections.singleton(step));
	}
}
