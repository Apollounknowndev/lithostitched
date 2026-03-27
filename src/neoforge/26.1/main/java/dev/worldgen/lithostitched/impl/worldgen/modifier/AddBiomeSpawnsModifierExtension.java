package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.List;

@ClassExtension(AddBiomeSpawnsModifier.class)
public class AddBiomeSpawnsModifierExtension implements NeoforgeModifierHolder {
	@ExtensionShadow
	private final HolderSet<Biome> biomes;
	@ExtensionShadow
	private final List<WeightedSpawnerData> biomeSpawns;
	
	@ExtensionInject
	@Override
	public BiomeModifier createNeoforgeModifier() {
		return new BiomeModifiers.AddSpawnsBiomeModifier(biomes, WeightedList.of(
			biomeSpawns
			.stream()
			.map(data -> new Weighted<>(new MobSpawnSettings.SpawnerData(data.type(), data.minCount(), data.maxCount()), data.weight()))
			.toList()
		));
	}
}
