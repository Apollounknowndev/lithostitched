package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.attribute.modifier.MobSpawnSettingsModifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

import java.util.Optional;

//? if neoforge {
/*import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
*///? }

public record AddBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, List<WeightedSpawnerData> biomeSpawns) implements WorldgenModifier /*? if neoforge{*//*, NeoforgeModifierHolder *//*?}*/ {
    public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        ExtraCodecs.compactListCodec(WeightedSpawnerData.CODEC).fieldOf("spawners").forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    ).apply(instance, AddBiomeSpawnsModifier::new));
    
    //? if neoforge {
    /*@Override
    public BiomeModifier createNeoforgeModifier() {
        return new BiomeModifiers.AddSpawnsBiomeModifier(biomes, WeightedList.of(
            biomeSpawns
                .stream()
                .map(data -> new Weighted<>(new MobSpawnSettings.SpawnerData(data.type(), data.count()), data.weight()))
                .toList()
        ));
    }
    *///? }
    
    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        //if (true) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(registries, entry);
        }
    }
    
    public void applyModifier(RegistryAccess registries, Holder<Biome> biome) {
        var attributeBuilder = EnvironmentAttributeMap.builder();
        attributeBuilder.putAll(biome.value().getAttributes());
	    
	    EnvironmentAttributeMap.Entry<MobSpawnSettings, ?> spawnEntry = biome.value().getAttributes().get(EnvironmentAttributes.NATURAL_MOB_SPAWNS);
	    
	    var spawnBuilder = new MobSpawnSettings.Builder();
	    if (spawnEntry != null) {
		    AttributeModifier<MobSpawnSettings, MobSpawnSettings> modifier = (AttributeModifier<MobSpawnSettings, MobSpawnSettings>) spawnEntry.modifier();
            MobSpawnSettings settings = (MobSpawnSettings) spawnEntry.argument();
            
            for (MobCategory category : settings.definedCategories()) {
                var mobsInCategory = settings.getMobsInCategory(category);
                if (mobsInCategory == null) continue;
                spawnBuilder.addAllSpawns(category, mobsInCategory);
            }
            spawnBuilder.addAllCosts(settings.allSpawnCosts());
            
            for (WeightedSpawnerData injectedData : this.biomeSpawns) {
                spawnBuilder.addSpawn(injectedData.type(), injectedData.weight(), injectedData.count());
            }
            
            attributeBuilder.modify(EnvironmentAttributes.NATURAL_MOB_SPAWNS, modifier, spawnBuilder.build());
        } else {
		    for (WeightedSpawnerData injectedData : this.biomeSpawns) {
                spawnBuilder.addSpawn(injectedData.type(), injectedData.weight(), injectedData.count());
            }
            
            attributeBuilder.modify(EnvironmentAttributes.NATURAL_MOB_SPAWNS, MobSpawnSettingsModifier.overlay(), spawnBuilder.build());
        }
        
        ((BiomeAccessor)(Object)biome.value()).setAttributes(attributeBuilder.build());
        WorldgenModifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.BIOME), biome);
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}

