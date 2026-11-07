package dev.worldgen.lithostitched.worldgen.modifier.internal;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.mixin.common.NoiseBasedChunkGeneratorAccessor;
import dev.worldgen.lithostitched.worldgen.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.densityfunction.MergedDensityFunction;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;

import java.util.Map;

/**
 * Before Lithostitched 1.4.12, if surface rule injections were applied to a dimension,
 * the entire dimension's `noise_settings` file was inlined.
 * <p>
 * This modifier converts those inline `noise_settings` back into references if a match
 * in the registry is found.
 */
public record RereferenceNoiseSettingsModifier() implements Modifier {
    public static final Codec<RereferenceNoiseSettingsModifier> CODEC = Codec.unit(RereferenceNoiseSettingsModifier::new);
    
    @Override
    public void applyModifier(RegistryAccess registries) {
        var dimensions = registries.registryOrThrow(Registries.LEVEL_STEM);
        var noiseSettings = registries.registryOrThrow(Registries.NOISE_SETTINGS);
        
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            if (!(entry.getValue().generator() instanceof NoiseBasedChunkGenerator generator)) continue;
            
            // Referenced noise settings are fine
            Holder<NoiseGeneratorSettings> savedSettings = generator.generatorSettings();
            if (savedSettings.kind().equals(Holder.Kind.REFERENCE)) continue;
            
            // Find match
            for (Holder<NoiseGeneratorSettings> registrySettings : noiseSettings.asHolderIdMap()) {
                if (doSettingsMatchIgnoringSurfaceRules(savedSettings.value(), registrySettings.value(), registrySettings)) {
                    ((NoiseBasedChunkGeneratorAccessor)(Object)generator).setSettings(registrySettings);
                    LithostitchedCommon.LOGGER.warn(
                        "Patched a possible corruption issue in the world save from previous Lithostitched versions. " +
                        "If there are new issues in this world starting right now, please report them to Lithostitched."
                    );
                    break;
                }
            }
        }
    }
    
    private static boolean doSettingsMatchIgnoringSurfaceRules(NoiseGeneratorSettings saved, NoiseGeneratorSettings registry, Holder<NoiseGeneratorSettings> holder) {
        if (!saved.noiseSettings().equals(registry.noiseSettings())) return false;
        if (!saved.defaultBlock().equals(registry.defaultBlock())) return false;
        if (!saved.defaultFluid().equals(registry.defaultFluid())) return false;
        if (!doDepthNoiseRouterValuesMatch(saved.noiseRouter(), registry.noiseRouter())) return false;
        if (!saved.spawnTarget().equals(registry.spawnTarget())) return false;
        if (saved.seaLevel() != registry.seaLevel()) return false;
        if (saved.disableMobGeneration() != registry.disableMobGeneration()) return false;
        if (saved.aquifersEnabled() != registry.aquifersEnabled()) return false;
        if (saved.oreVeinsEnabled() != registry.oreVeinsEnabled()) return false;
        if (saved.useLegacyRandomSource() != registry.useLegacyRandomSource()) return false;
        return true;
    }
    
    private static boolean doDepthNoiseRouterValuesMatch(NoiseRouter saved, NoiseRouter registry) {
        ResourceLocation savedId = getDensityFunctionId(NoiseRouterTarget.DEPTH.getDensityFunction(saved));
        ResourceLocation registryId = getDensityFunctionId(NoiseRouterTarget.DEPTH.getDensityFunction(registry));
	    
	    return savedId != null && savedId.equals(registryId);
    }
    
    private static ResourceLocation getDensityFunctionId(DensityFunction function) {
        if (function instanceof MergedDensityFunction merged) {
            function = merged.original();
        }
        if (function instanceof DensityFunctions.HolderHolder holderHolder) {
            if (holderHolder.function().unwrapKey().isPresent()) {
                return holderHolder.function().unwrapKey().get().location();
            }
        }
        return null;
    }
	
	@Override
	public ModifierPredicate getPredicate() {
		return new TrueModifierPredicate();
	}
	
	@Override
	public ModifierPhase getPhase() {
		return ModifierPhase.AFTER_ALL;
	}
	
	@Override
	public void applyModifier() {
	
	}
	
	@Override
	public Codec<? extends Modifier> codec() {
		return CODEC;
	}
}
