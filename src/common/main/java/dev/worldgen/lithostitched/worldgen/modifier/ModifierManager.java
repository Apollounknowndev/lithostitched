package dev.worldgen.lithostitched.worldgen.modifier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ModifierManager {
    public static void applyModifiers(MinecraftServer server) {
        boolean fabricFeaturesModified = false;
        RegistryAccess registries = server.registryAccess();
	    
	    List<Pair<Identifier, WorldgenModifier>> modifiers = new ArrayList<>(
            registries.lookupOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER).listElements().map(ModifierManager::pair).toList()
        );
        AddWorldgenModifiersEvent.EVENT.invoker().addModifiers(registries, (id, modifier) -> modifiers.add(new Pair<>(id, modifier)));

        for (Pair<Identifier, WorldgenModifier> pair : sortByPriority(modifiers)) {
            Lithostitched.debug("Applying modifier with id: {}", pair.getFirst());
            pair.getSecond().apply(registries);

            if (pair.getSecond().shouldRecompileSortedFeatures()) {
                fabricFeaturesModified = true;
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

    static List<Pair<Identifier, WorldgenModifier>> sortByPriority(List<Pair<Identifier, WorldgenModifier>> modifiers) {
        return modifiers.stream().sorted(Comparator.comparingInt(pair -> pair.getSecond().priority())).toList();
    }
    
    private static Pair<Identifier, WorldgenModifier> pair(Holder.Reference<WorldgenModifier> holder) {
        return new Pair<>(holder.key().identifier(), holder.value());
    }
}
