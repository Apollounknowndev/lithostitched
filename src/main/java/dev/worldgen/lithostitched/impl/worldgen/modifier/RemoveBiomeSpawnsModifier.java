package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

//? if neoforge {
/*import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
*///? }

public record RemoveBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) implements WorldgenModifier /*? if neoforge{*//*, NeoforgeModifierHolder *//*?}*/ {
    public static final MapCodec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_REMOVE_CODEC.forGetter(RemoveBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
        RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
    ).apply(instance, RemoveBiomeSpawnsModifier::new));
    
    //? if neoforge {
    /*@Override
    public BiomeModifier createNeoforgeModifier() {
        return new BiomeModifiers.RemoveSpawnsBiomeModifier(biomes, mobs);
    }
    *///? }
    
    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        //if (true) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
        }
    }
    
    public void applyModifier(Biome biome) {
        MobSpawnSettings biomeMobSettings = biome.getMobSettings();
        HashMap<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());
        for (MobCategory category : MobCategory.values()) {
            List<Weighted<MobSpawnSettings.SpawnerData>> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
            categorySpawnList.removeIf(mobEntry -> this.mobs.contains(mobEntry.value().type().builtInRegistryHolder()));
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
