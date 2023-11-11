package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link Modifier} implementation that removes mob spawns from {@link Biome} entries.
 *
 * @author Apollo
 */
public class RemoveBiomeSpawnsModifier extends Modifier {
    public static final Codec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
        RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
    )).apply(instance, RemoveBiomeSpawnsModifier::new));
    private final HolderSet<Biome> biomes;
    private final HolderSet<EntityType<?>> mobs;
    protected RemoveBiomeSpawnsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) {
        super(predicate, ModifierPhase.REMOVE);
        this.biomes = biomes;
        this.mobs = mobs;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public HolderSet<EntityType<?>> mobs() {
        return this.mobs;
    }
    public List<EntityType<?>> entityTypes() {
        List<EntityType<?>> entityTypes = new ArrayList<>();
        for (Holder<EntityType<?>> entry : this.mobs()) {
            entityTypes.add(entry.value());
        }
        return entityTypes;
    }

    public void applyModifier(Biome biome) {
        MobSpawnSettings biomeMobSettings = biome.getMobSettings();
        HashMap<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
        for (MobCategory category : MobCategory.values()) {
            List<MobSpawnSettings.SpawnerData> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
            categorySpawnList.removeIf(mobEntry -> this.entityTypes().contains(mobEntry.type));
            spawners.put(category, WeightedRandomList.create(categorySpawnList));
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
