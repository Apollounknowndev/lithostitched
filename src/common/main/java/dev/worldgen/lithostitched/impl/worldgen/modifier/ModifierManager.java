package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.*;
import java.util.stream.Collectors;

public class ModifierManager {
    // TODO: This is probably unsafe, clear out in server start?
    public static final Map<Identifier, WorldgenModifier> MODIFIERS = new HashMap<>();
    
    public static void applyModifiers(MinecraftServer server) {
        boolean fabricFeaturesModified = false;
        RegistryAccess registries = server.registryAccess();
        
        MODIFIERS.clear();
        registries.lookupOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER).listElements().forEach(holder -> MODIFIERS.put(holder.key().identifier(), holder.value()));
        AddWorldgenModifiersEvent.EVENT.invoker().addModifiers(registries, (id, modifier) -> {
            if (!MODIFIERS.containsKey(id)) {
                MODIFIERS.put(id, modifier);
            }
        });

        for (Map.Entry<Identifier, WorldgenModifier> entry : sortByPriority(MODIFIERS)) {
            Lithostitched.debug("Applying modifier with id: {}", entry.getKey());
            entry.getValue().apply(registries);

            if (entry.getValue().shouldRecompileSortedFeatures()) {
                fabricFeaturesModified = LithostitchedPlatform.isFabric();
            }
        }

        if (fabricFeaturesModified) {
            for (LevelStem dimension : Lithostitched.registry(registries, Registries.LEVEL_STEM).stream().toList()) {
                var accessor = ((ChunkGeneratorAccessor)dimension.generator());
                BiomeSource source = accessor.getBiomeSource();
                accessor.setFeaturesPerStep(Suppliers.memoize(() ->
                    FeatureSorter.buildFeaturesPerStep(List.copyOf(source.possibleBiomes()), biome -> accessor.getGetter().apply(biome).features(), true)
                ));
            }
        }
    }

    static List<Map.Entry<Identifier, WorldgenModifier>> sortByPriority(Map<Identifier, WorldgenModifier> modifiers) {
        return modifiers.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().priority())).toList();
    }
}
