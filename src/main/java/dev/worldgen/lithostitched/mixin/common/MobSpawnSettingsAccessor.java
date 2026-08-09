package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MobSpawnSettings.class)
public interface MobSpawnSettingsAccessor {
    @Accessor
    Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> getSpawnsByCategory();
    
    @Accessor("spawnsByCategory")
    void setSpawnsByCategory(Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> spawnsByCategory);
}
