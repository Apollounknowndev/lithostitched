package dev.worldgen.lithostitched.worldgen.surface;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.TransientMergedRule;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.mixin.common.NoiseGeneratorSettingsAccessor;
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
 * The manager class for material rule injection.
 *
 * @author Apollo
*/
public class MaterialRuleManager {
    public static void applySurfaceRules(RegistryAccess registries, Registry<LevelStem> dimensions) {
	    List<Map.Entry<Identifier, AddSurfaceRuleModifier>> materialRules = ModifierManager.getModifiersOfType(registries, AddSurfaceRuleModifier.CODEC);
        if (materialRules.isEmpty()) return;

        HashMap<Identifier, ArrayList<Pair<Identifier, AddSurfaceRuleModifier>>> assignedSurfaceRules = new HashMap<>();
        for (Map.Entry<Identifier, AddSurfaceRuleModifier> entry : materialRules) {
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
                ((NoiseGeneratorSettingsAccessor)(Object)settings).setSurfaceRule(Holder.direct(
                    buildModdedSurfaceRules(surfaceRulesForKey, settings.materialRule().value())
                ));

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
