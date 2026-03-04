package dev.worldgen.lithostitched.impl.worldgen.modifier;

import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveBiomeSpawnsModifierActual {
	@Actual
	public void applyModifier(Biome biome) {
		MobSpawnSettings biomeMobSettings = biome.getMobSettings();
		HashMap<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
		var mobs = ((RemoveBiomeSpawnsModifier)(Object)this).mobs();
		for (MobCategory category : MobCategory.values()) {
			List<MobSpawnSettings.SpawnerData> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
			categorySpawnList.removeIf(mobEntry -> mobs.contains(mobEntry.type.builtInRegistryHolder()));
			spawners.put(category, WeightedRandomList.create(categorySpawnList));
		}
		((MobSpawnSettingsAccessor)biomeMobSettings).setSpawners(spawners);
		((BiomeAccessor)(Object)biome).setMobSettings(biomeMobSettings);
	}
}
