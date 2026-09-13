package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;

public record RemoveBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) implements WorldgenModifier {
    public static final MapCodec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_REMOVE_CODEC.forGetter(RemoveBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
        RegistryCodecs.holderSet(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
    ).apply(instance, RemoveBiomeSpawnsModifier::new));
    
    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        //if (true) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(registries, entry);
        }
    }
    
    public void applyModifier(RegistryAccess registries, Holder<Biome> biome) {
        EnvironmentAttributeMap.Entry<MobSpawnSettings, ?> spawnEntry = biome.value().getAttributes().get(EnvironmentAttributes.NATURAL_MOB_SPAWNS);
        if (spawnEntry == null) return;
        
        var attributeBuilder = EnvironmentAttributeMap.builder();
        attributeBuilder.putAll(biome.value().getAttributes());
        
        var spawnBuilder = new MobSpawnSettings.Builder();
        AttributeModifier<MobSpawnSettings, MobSpawnSettings> modifier = (AttributeModifier<MobSpawnSettings, MobSpawnSettings>) spawnEntry.modifier();
        MobSpawnSettings settings = (MobSpawnSettings) spawnEntry.argument();
        
        for (MobCategory category : settings.definedCategories()) {
            var mobsInCategory = settings.getMobsInCategory(category);
            if (mobsInCategory == null) continue;
            mobsInCategory.unwrap().forEach(weighted -> {
                MobSpawnSettings.SpawnerData data = weighted.value();
                if (this.mobs.contains(data.type().builtInRegistryHolder())) return;
                spawnBuilder.addSpawn(data.type(), weighted.weight(), data.count());
            });
        }
        
        attributeBuilder.modify(EnvironmentAttributes.NATURAL_MOB_SPAWNS, modifier, spawnBuilder.build());
        
        ((BiomeAccessor)(Object)biome.value()).setAttributes(attributeBuilder.build());
        WorldgenModifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.BIOME), biome);
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
