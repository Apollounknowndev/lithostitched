package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.platform.LithostitchedPlatform;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.*;

public class ModifierManager {
    
    public static void applyModifiers(RegistryAccess registries, Registry<LevelStem> dimensions) {
        boolean recompileSortedFeatures = false;
        
        Map<ResourceLocation, WorldgenModifier> modifiers = getAllModifiers(registries);
        for (Map.Entry<ResourceLocation, WorldgenModifier> entry : sortByPriority(modifiers)) {
            Lithostitched.debug("Applying modifier with id: {}", entry.getKey());
            entry.getValue().apply(registries);

            if (entry.getValue().shouldRecompileSortedFeatures()) {
                recompileSortedFeatures = true;
            }
        }
        
        //? if neoforge
        if (true) return;
        
        if (recompileSortedFeatures) {
            for (LevelStem dimension : dimensions.stream().toList()) {
                var accessor = ((ChunkGeneratorAccessor)dimension.generator());
                BiomeSource source = accessor.getBiomeSource();
                accessor.setFeaturesPerStep(LithostitchedPlatform.memoize(() ->
                    FeatureSorter.buildFeaturesPerStep(List.copyOf(source.possibleBiomes()), biome -> accessor.getGetter().apply(biome).features(), true)
                ));
            }
        }
    }

    static List<Map.Entry<ResourceLocation, WorldgenModifier>> sortByPriority(Map<ResourceLocation, WorldgenModifier> modifiers) {
        return modifiers.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().priority())).toList();
    }
    
    public static Map<ResourceLocation, WorldgenModifier> getAllModifiers(RegistryAccess registries) {
        Map<ResourceLocation, WorldgenModifier> modifiers = new HashMap<>();
        registries.lookupOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER).listElements().forEach(holder -> modifiers.put(holder.key().location(), holder.value()));
        AddWorldgenModifiersEvent.EVENT.invoker().addModifiers(registries, (id, modifier) -> {
            if (!modifiers.containsKey(id)) {
                modifiers.put(id, modifier);
            }
        });
        return modifiers;
    }
    
    public static <T> List<Map.Entry<ResourceLocation, T>> getModifiersOfType(RegistryAccess registries, MapCodec<T> codec) {
        Map<ResourceLocation, WorldgenModifier> modifiers = getAllModifiers(registries);
        return modifiers.entrySet().stream().filter(entry -> entry.getValue().codec().equals(codec)).map(entry -> Map.entry(entry.getKey(), (T) entry.getValue())).toList();
    }
}
