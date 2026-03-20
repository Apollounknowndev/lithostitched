package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.registry.LithostitchedNeoforgeBiomeModifiers;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.BiomeModifier;

@ClassExtension(ReplaceEffectsModifier.class)
public class ReplaceEffectsModifierExtension implements NeoforgeModifierHolder {
	@ExtensionShadow
	private final HolderSet<Biome> biomes;
	@ExtensionShadow
	private final BiomeEffects effects;
	
	@ExtensionInject
	@Override
	public BiomeModifier createNeoforgeModifier() {
		return new LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier(biomes, effects);
	}
}
