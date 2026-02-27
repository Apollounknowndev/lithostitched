package dev.worldgen.lithostitched.worldgen.modifier;

import com.google.common.base.Suppliers;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ModifierManager {
    public static void applyModifiers(MinecraftServer server) {
        boolean fabricFeaturesModified = false;
        RegistryAccess registries = server.registryAccess();
        HolderLookup.RegistryLookup<WorldgenModifier> modifiers = registries.lookupOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);

        for (Holder.Reference<WorldgenModifier> reference : sortByPriority(modifiers.listElements())) {
            Lithostitched.debug("Applying modifier with id: {}", reference.key().identifier());
            reference.value().apply(registries);

            if (reference.value().shouldRecompileSortedFeatures()) {
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

    static List<Holder.Reference<WorldgenModifier>> sortByPriority(Stream<Holder.Reference<WorldgenModifier>> modifiers) {
        return modifiers.sorted(Comparator.comparingInt(reference -> reference.value().priority())).toList();
    }
}
