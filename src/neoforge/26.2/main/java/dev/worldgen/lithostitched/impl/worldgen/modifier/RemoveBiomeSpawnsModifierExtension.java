package dev.worldgen.lithostitched.impl.worldgen.modifier;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ClassExtension(RemoveBiomeSpawnsModifier.class)
public class RemoveBiomeSpawnsModifierExtension implements NeoforgeModifierHolder {
	@ExtensionShadow
	private final HolderSet<Biome> biomes;
	@ExtensionShadow
	private final HolderSet<EntityType<?>> mobs;
	
	@ExtensionInject
	@Override
	public BiomeModifier createNeoforgeModifier() {
		return new BiomeModifiers.RemoveSpawnsBiomeModifier(biomes, mobs);
	}
}
