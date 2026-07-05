package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

import static dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier.modifyDensityFunction;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;"
        )
    )
    private RandomState wrapNoiseRouter(NoiseGeneratorSettings noiseSettings, HolderGetter<NormalNoise.NoiseParameters> noiseGetter, long seed, Operation<RandomState> init, ServerLevel level, @Local(ordinal = 0) RegistryAccess registries) {
        NoiseGeneratorSettingsAccessor accessor = ((NoiseGeneratorSettingsAccessor)(Object)noiseSettings);
        NoiseRouter router = noiseSettings.noiseRouter();

        ResourceKey<Level> dimension = level.dimension();
        List<WrapNoiseRouterModifier> modifiers = ModifierManager.getModifiersOfType(registries, WrapNoiseRouterModifier.CODEC).stream()
            .filter(entry -> {
                ResourceKey<Level> declared = entry.getValue().dimension();
                if (declared == null) {
                    Lithostitched.LOGGER.warn("Wrap noise router modifier {} has no dimension; applying to all dimensions", entry.getKey());
                    return true;
                }
                return declared.equals(dimension);
            })
            .map(Map.Entry::getValue)
            .toList();

        if (!modifiers.isEmpty()) {
            accessor.setNoiseRouter(new NoiseRouter(
                modifyDensityFunction(NoiseRouterTarget.BARRIER, router.barrierNoise(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.FLUID_LEVEL_FLOODEDNESS, router.fluidLevelFloodednessNoise(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.FLUID_LEVEL_SPREAD, router.fluidLevelSpreadNoise(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.LAVA, router.lavaNoise(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.TEMPERATURE, router.temperature(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.VEGETATION, router.vegetation(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.CONTINENTS, router.continents(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.EROSION, router.erosion(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.DEPTH, router.depth(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.RIDGES, router.ridges(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.INITIAL_DENSITY, Lithostitched.getInitialDensity(router), modifiers),
                modifyDensityFunction(NoiseRouterTarget.FINAL_DENSITY, router.finalDensity(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.VEIN_TOGGLE, router.veinToggle(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.VEIN_RIDGED, router.veinRidged(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.VEIN_GAP, router.veinGap(), modifiers)
            ));
        }

        return init.call(noiseSettings, noiseGetter, seed);
    }
}
