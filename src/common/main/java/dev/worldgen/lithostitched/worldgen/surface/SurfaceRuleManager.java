package dev.worldgen.lithostitched.worldgen.surface;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.mixin.common.NoiseBasedChunkGeneratorAccessor;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier.InjectionType;
import dev.worldgen.lithostitched.worldgen.surface.rule.TransientMergedRule;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The manager class for surface rule injection.
 *
 * @author Apollo
*/
public class SurfaceRuleManager {
    @SuppressWarnings("deprecation")
    public static void applySurfaceRules(MinecraftServer server) {
        RegistryAccess registries = server.registryAccess();
	    Set<Map.Entry<Identifier, WorldgenModifier>> surfaceRules = ModifierManager.MODIFIERS.entrySet().stream().filter(entry -> entry.getValue() instanceof AddSurfaceRuleModifier).collect(Collectors.toSet());
        if (surfaceRules.isEmpty()) return;

        HashMap<Identifier, ArrayList<Pair<Identifier, AddSurfaceRuleModifier>>> assignedSurfaceRules = new HashMap<>();
        for (Map.Entry<Identifier, WorldgenModifier> entry : surfaceRules) {
            AddSurfaceRuleModifier modifier = (AddSurfaceRuleModifier)entry.getValue();
            modifier.levels().forEach(levelStemResourceKey -> assignedSurfaceRules.computeIfAbsent(levelStemResourceKey.identifier(), __ -> new ArrayList<>()).add(Pair.of(entry.getKey(), modifier)));
        }

        Registry<LevelStem> dimensions = Lithostitched.registry(registries, Registries.LEVEL_STEM);
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            Identifier location = entry.getKey().identifier();
            var surfaceRulesForKey = assignedSurfaceRules.get(location);
            if (surfaceRulesForKey != null) {
                if (!(entry.getValue().generator() instanceof NoiseBasedChunkGenerator generator)) continue;
                NoiseGeneratorSettings settings = generator.generatorSettings().value();
                SurfaceRules.RuleSource oldRules = settings.surfaceRule();
                // Noise generator settings must be rebuilt due to Forge not allowing surface rules to be directly modified.
                ((NoiseBasedChunkGeneratorAccessor)(Object)generator).setSettings(Holder.direct(new NoiseGeneratorSettings(
                    settings.noiseSettings(),
                    settings.defaultBlock(),
                    settings.defaultFluid(),
                    settings.noiseRouter(),
                    buildModdedSurfaceRules(surfaceRulesForKey, oldRules),
                    settings.spawnTarget(),
                    settings.seaLevel(),
                    settings.disableMobGeneration(),
                    settings.isAquifersEnabled(),
                    settings.oreVeinsEnabled(),
                    settings.useLegacyRandomSource()
                )));

                Lithostitched.debug("Applied {} surface rule additions for '{}' dimension", surfaceRulesForKey.size(), location);
            }
        }
    }

    private static SurfaceRules.RuleSource buildModdedSurfaceRules(ArrayList<Pair<Identifier, AddSurfaceRuleModifier>> surfaceInjections, SurfaceRules.RuleSource originalSource) {
        // TODO: Implement caching
        List<SurfaceRules.RuleSource> sources = new ArrayList<>();
        surfaceInjections.sort(Comparator.comparingInt(pair -> pair.getSecond().priority()));
        surfaceInjections.forEach(pair -> {
            if (pair.getSecond().injectionType() == InjectionType.PREPEND) {
                sources.add(pair.getSecond().surfaceRule());
            }
        });
        sources.add(originalSource);
        surfaceInjections.forEach(pair -> {
            if (pair.getSecond().injectionType() == InjectionType.APPEND) {
                sources.add(pair.getSecond().surfaceRule());
            }
        });
        
        if (originalSource instanceof TransientMergedRule transientMerged) {
            transientMerged.prependedRules().addAll(sources);
            return originalSource;
        } else {
            return new TransientMergedRule(sources, originalSource);
        }
    }
}
