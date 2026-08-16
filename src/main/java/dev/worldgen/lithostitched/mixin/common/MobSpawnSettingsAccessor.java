package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MobSpawnSettings.class)
public interface MobSpawnSettingsAccessor {
    @Accessor("spawners")
    Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> getSpawners();
    
    @Accessor("spawners")
    void setSpawners(Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners);
    
    @Accessor("mobSpawnCosts")
    Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> lithostitched$getSpawnCosts();
    
    @Accessor("mobSpawnCosts")
    void lithostitched$setSpawnCosts(Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> spawnCosts);
}
