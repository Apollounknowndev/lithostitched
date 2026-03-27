package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.registry.LithostitchedNeoforgeBiomeModifiers;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.BiomeModifier;

@ClassExtension(ReplaceClimateModifier.class)
public class ReplaceClimateModifierExtension implements NeoforgeModifierHolder {
	@ExtensionShadow
	private final HolderSet<Biome> biomes;
	@ExtensionShadow
	private final BiomeClimate climateSettings;
	
	@ExtensionInject
	@Override
	public BiomeModifier createNeoforgeModifier() {
		return new LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier(biomes, climateSettings);
	}
}
