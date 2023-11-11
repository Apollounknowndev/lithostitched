package dev.worldgen.lithostitched.worldgen.surface;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The manager class for surface rule injection.
 *
 * @author Apollo
*/
public class SurfaceRuleManager {

    @SuppressWarnings("deprecation")
    public static void applySurfaceRules(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        var surfaceRules = registryAccess.registryOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER).entrySet().stream().filter((entry) -> entry.getValue() instanceof AddSurfaceRuleModifier).collect(Collectors.toSet());
        if (surfaceRules.isEmpty()) return;

        HashMap<ResourceLocation, ArrayList<Pair<ResourceLocation, AddSurfaceRuleModifier>>> assignedSurfaceRules = new HashMap<>();
        for (var assignedSurfaceRule : surfaceRules) {
            AddSurfaceRuleModifier slice = (AddSurfaceRuleModifier)assignedSurfaceRule.getValue();
            slice.levels().forEach(levelStemResourceKey -> assignedSurfaceRules.computeIfAbsent(levelStemResourceKey.location(), __ -> new ArrayList<>()).add(Pair.of(assignedSurfaceRule.getKey().location(), slice)));
        }

        Registry<LevelStem> dimensions = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            ResourceLocation location = entry.getKey().location();
            var surfaceRulesForKey = assignedSurfaceRules.get(location);
            if (surfaceRulesForKey != null) {
                ChunkGenerator chunkGenerator = entry.getValue().generator();
                if (!(chunkGenerator instanceof NoiseBasedChunkGenerator)) continue;
                NoiseGeneratorSettings settings = ((NoiseBasedChunkGenerator) chunkGenerator).generatorSettings().value();
                SurfaceRules.RuleSource oldRules = settings.surfaceRule();
                // Noise generator settings must be rebuilt due to Forge not allowing surface rules to be directly modified.
                ((NoiseBasedChunkGenerator) chunkGenerator).settings = Holder.direct(new NoiseGeneratorSettings(
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
                ));

                LithostitchedCommon.LOGGER.info("Applied " + surfaceRulesForKey.size() + " surface rule additions for '" + location + "' dimension");
            }
        }
    }

    private static SurfaceRules.RuleSource buildModdedSurfaceRules(ArrayList<Pair<ResourceLocation, AddSurfaceRuleModifier>> moddedSourceList, SurfaceRules.RuleSource originalSource) {
        // TODO: Implement caching
        List<SurfaceRules.RuleSource> newRuleSourceList = new ArrayList<>();
        moddedSourceList.forEach((pair) -> newRuleSourceList.add(pair.getSecond().surfaceRule()));

        newRuleSourceList.add(originalSource);
        if (originalSource instanceof LithostitchedSurfaceRules.TransientMergedRuleSource) {
            ((LithostitchedSurfaceRules.TransientMergedRuleSource) originalSource).sequence().addAll(newRuleSourceList);
            return originalSource;
        } else {
            return new LithostitchedSurfaceRules.TransientMergedRuleSource(newRuleSourceList, originalSource);
        }
    }
}
