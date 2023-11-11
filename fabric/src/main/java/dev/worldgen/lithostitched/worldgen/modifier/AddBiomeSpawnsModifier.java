package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
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
public class AddBiomeSpawnsModifier extends Modifier {
    public static final Codec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            MobSpawnSettings.SpawnerData.CODEC.listOf().fieldOf("spawners"),
            MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawners")
        ).xmap(
            to -> to.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    )).apply(instance, AddBiomeSpawnsModifier::new));
    private final HolderSet<Biome> biomes;
    private final List<MobSpawnSettings.SpawnerData> biomeSpawns;
    protected AddBiomeSpawnsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> biomeSpawns) {
        super(predicate, ModifierPhase.ADD);
        this.biomes = biomes;
        this.biomeSpawns = biomeSpawns;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public List<MobSpawnSettings.SpawnerData> biomeSpawns() {
        return this.biomeSpawns;
    }

    public void applyModifier(Biome biome) {
        MobSpawnSettings biomeMobSettings = biome.getMobSettings();
        HashMap<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
        for (MobSpawnSettings.SpawnerData spawnerEntry : this.biomeSpawns()) {
            List<MobSpawnSettings.SpawnerData> categorySpawnList = new ArrayList<>(spawners.get(spawnerEntry.type.getCategory()).unwrap());
            categorySpawnList.add(spawnerEntry);
            spawners.put(spawnerEntry.type.getCategory(), WeightedRandomList.create(categorySpawnList));
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
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
