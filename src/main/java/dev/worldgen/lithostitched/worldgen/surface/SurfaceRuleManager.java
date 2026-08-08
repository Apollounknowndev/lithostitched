package dev.worldgen.lithostitched.worldgen.surface;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.TransientMergedRule;
import net.minecraft.util.valueproviders.IntProviders;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.mixin.common.NoiseBasedChunkGeneratorAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.*;

/**
 * The manager class for surface rule injection.
 *
 * @author Apollo
*/
public class SurfaceRuleManager {
    @SuppressWarnings("deprecation")
    public static void applySurfaceRules(RegistryAccess registries, Registry<LevelStem> dimensions) {
	    List<Map.Entry<Identifier, AddSurfaceRuleModifier>> surfaceRules = ModifierManager.getModifiersOfType(registries, AddSurfaceRuleModifier.CODEC);
        if (surfaceRules.isEmpty()) return;

        HashMap<Identifier, ArrayList<Pair<Identifier, AddSurfaceRuleModifier>>> assignedSurfaceRules = new HashMap<>();
        for (Map.Entry<Identifier, AddSurfaceRuleModifier> entry : surfaceRules) {
            entry.getValue().levels().forEach(level ->
                assignedSurfaceRules.computeIfAbsent(level.identifier(), __ -> new ArrayList<>()).add(Pair.of(entry.getKey(), entry.getValue()))
            );
        }

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

    private static SurfaceRules.RuleSource buildModdedSurfaceRules(ArrayList<Pair<Identifier, AddSurfaceRuleModifier>> surfaceInjections, SurfaceRules.RuleSource original) {
        // TODO: Implement caching
        List<SurfaceRules.RuleSource> additions = new ArrayList<>();
        surfaceInjections.sort(Comparator.comparingInt(pair -> pair.getSecond().priority()));
        surfaceInjections.forEach(pair -> {
            if (pair.getSecond().injectionType() == InjectionType.PREPEND) {
                additions.add(pair.getSecond().surfaceRule());
            }
        });
        additions.add(original);
        surfaceInjections.forEach(pair -> {
            if (pair.getSecond().injectionType() == InjectionType.APPEND) {
                additions.add(pair.getSecond().surfaceRule());
            }
        });
        
        if (original instanceof TransientMergedRule transientMerged) {
            transientMerged.rules().addAll(additions);
            return original;
        } else {
            return new TransientMergedRule(additions, original);
        }
    }
}
