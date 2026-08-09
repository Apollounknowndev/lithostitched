package dev.worldgen.lithostitched.worldgen.modifier.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.duck.StructurePoolAccess;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.mixin.common.NoiseBasedChunkGeneratorAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Map;
import java.util.Optional;

/**
 * Before Lithostitched 1.8.0, if surface rule injections were applied to a dimension,
 * the entire dimension's `noise_settings` file was inlined.
 * <p>
 * This modifier converts those inline `noise_settings` back into references if a match
 * in the registry is found.
 */
public record RereferenceNoiseSettingsModifier() implements WorldgenModifier {
    public static final MapCodec<RereferenceNoiseSettingsModifier> CODEC = MapCodec.unit(RereferenceNoiseSettingsModifier::new);
    
    @Override
    public Optional<LoadPredicate> predicate() {
        return Optional.empty();
    }
    
    @Override
    public void apply(RegistryAccess registries) {
        var dimensions = Lithostitched.registry(registries, Registries.LEVEL_STEM);
        var noiseSettings = Lithostitched.registry(registries, Registries.NOISE_SETTINGS);
        
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            if (!(entry.getValue().generator() instanceof NoiseBasedChunkGenerator generator)) continue;
            
            // Referenced noise settings are fine
            Holder<NoiseGeneratorSettings> savedSettings = generator.generatorSettings();
            if (savedSettings.kind().equals(Holder.Kind.REFERENCE)) continue;
            
            // Find match
            for (Holder<NoiseGeneratorSettings> registrySettings : noiseSettings.asHolderIdMap()) {
                if (doSettingsMatchIgnoringSurfaceRules(savedSettings.value(), registrySettings.value())) {
                    ((NoiseBasedChunkGeneratorAccessor)(Object)generator).setSettings(registrySettings);
                    Lithostitched.LOGGER.warn(
                        "Patched a possible memory leak in the world save from previous Lithostitched versions. " +
                        "If there are new issues in this world starting right now, please report them to Lithostitched."
                    );
                }
            }
        }
    }
    
    private static boolean doSettingsMatchIgnoringSurfaceRules(NoiseGeneratorSettings saved, NoiseGeneratorSettings registry) {
        if (!saved.noiseSettings().equals(registry.noiseSettings())) return false;
        if (!saved.defaultBlock().equals(registry.defaultBlock())) return false;
        if (!saved.defaultFluid().equals(registry.defaultFluid())) return false;
        if (!doNoiseRoutersMatch(saved.noiseRouter(), registry.noiseRouter())) return false;
        if (!saved.spawnTarget().equals(registry.spawnTarget())) return false;
        if (saved.seaLevel() != registry.seaLevel()) return false;
        if (saved.disableMobGeneration() != registry.disableMobGeneration()) return false;
        if (saved.aquifersEnabled() != registry.aquifersEnabled()) return false;
        if (saved.oreVeinsEnabled() != registry.oreVeinsEnabled()) return false;
        if (saved.useLegacyRandomSource() != registry.useLegacyRandomSource()) return false;
        return true;
    }
    
    private static boolean doNoiseRoutersMatch(NoiseRouter saved, NoiseRouter registry) {
        for (NoiseRouterTarget target : NoiseRouterTarget.values()) {
            if (!filterMergedRule(target.getDensityFunction(saved)).equals(target.getDensityFunction(registry))) {
                return false;
            }
        }
        return true;
    }
    
    private static DensityFunction filterMergedRule(DensityFunction function) {
        if (function instanceof MergedDensityFunction merged) {
            return merged.original();
        } else if (function instanceof DensityFunctions.HolderHolder(Holder<DensityFunction> holder)) {
            return holder.value();
        }
        return function;
    }
    
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
