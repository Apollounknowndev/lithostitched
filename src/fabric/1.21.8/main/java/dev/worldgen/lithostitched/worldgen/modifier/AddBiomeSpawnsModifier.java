package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link Modifier} implementation that adds mob spawn data to {@link Biome} entries.
 *
 * @author Apollo
 */
public record AddBiomeSpawnsModifier(int priority, HolderSet<Biome> biomes, List<Weighted<MobSpawnSettings.SpawnerData>> biomeSpawns) implements Modifier {
    private static final Codec<Weighted<MobSpawnSettings.SpawnerData>> SPAWNER_CODEC = Weighted.codec(MobSpawnSettings.SpawnerData.CODEC);

    public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(AddBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            SPAWNER_CODEC.listOf().fieldOf("spawners"),
            SPAWNER_CODEC.fieldOf("spawners")
        ).xmap(
            either -> either.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    ).apply(instance, AddBiomeSpawnsModifier::new));

    public void applyModifier(Biome biome) {
        MobSpawnSettings biomeMobSettings = biome.getMobSettings();
        HashMap<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
        for (Weighted<MobSpawnSettings.SpawnerData> spawnerEntry : this.biomeSpawns()) {
            List<Weighted<MobSpawnSettings.SpawnerData>> categorySpawnList = new ArrayList<>(spawners.get(spawnerEntry.value().type().getCategory()).unwrap());
            categorySpawnList.add(spawnerEntry);
            spawners.put(spawnerEntry.value().type().getCategory(), WeightedList.of(categorySpawnList));
        }
        ((MobSpawnSettingsAccessor)biomeMobSettings).setSpawners(spawners);
        ((BiomeAccessor)(Object)biome).setMobSettings(biomeMobSettings);
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());
        }
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
