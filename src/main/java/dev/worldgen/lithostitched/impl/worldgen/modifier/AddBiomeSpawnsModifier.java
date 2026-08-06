package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;

public record AddBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, List<WeightedSpawnerData> biomeSpawns) implements WorldgenModifier {
    public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            WeightedSpawnerData.CODEC.listOf().fieldOf("spawners"),
            WeightedSpawnerData.CODEC.fieldOf("spawners")
        ).xmap(
            either -> either.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    ).apply(instance, AddBiomeSpawnsModifier::new));
    
    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        if (true) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
        }
    }

    public void applyModifier(Biome biome) {
        MobSpawnSettings biomeMobSettings = biome.getMobSettings();
        HashMap<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
        for (WeightedSpawnerData spawner : this.biomeSpawns()) {
            MobCategory category = spawner.type().getCategory();
            List<Weighted<MobSpawnSettings.SpawnerData>> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
            categorySpawnList.add(new Weighted<>(new MobSpawnSettings.SpawnerData(spawner.type(), spawner.minCount(), spawner.maxCount()), spawner.weight()));
            spawners.put(category, WeightedList.of(categorySpawnList));
        }
        ((MobSpawnSettingsAccessor)biomeMobSettings).setSpawners(spawners);
        ((BiomeAccessor)(Object)biome).setMobSettings(biomeMobSettings);
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
    

}

