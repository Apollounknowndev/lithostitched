package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

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
    private RandomState wrapNoiseRouter(NoiseGeneratorSettings noiseSettings, HolderGetter<NormalNoise.NoiseParameters> noiseGetter, long seed, Operation<RandomState> init, @Local(ordinal = 0) RegistryAccess registries) {
        NoiseGeneratorSettingsAccessor accessor = ((NoiseGeneratorSettingsAccessor)(Object)noiseSettings);
        NoiseRouter router = noiseSettings.noiseRouter();

        List<WrapNoiseRouterModifier> modifiers = registries
            .registryOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER)
            .stream()
            .filter(WrapNoiseRouterModifier.class::isInstance)
            .map(WrapNoiseRouterModifier.class::cast)
            .toList();

        accessor.setNoiseRouter(new NoiseRouter(
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.BARRIER, router.barrierNoise(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.FLUID_LEVEL_FLOODEDNESS, router.fluidLevelFloodednessNoise(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.FLUID_LEVEL_SPREAD, router.fluidLevelSpreadNoise(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.LAVA, router.lavaNoise(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.TEMPERATURE, router.temperature(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.VEGETATION, router.vegetation(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.CONTINENTS, router.continents(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.EROSION, router.erosion(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.DEPTH, router.depth(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.RIDGES, router.ridges(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.INITIAL_DENSITY, router.initialDensityWithoutJaggedness(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.FINAL_DENSITY, router.finalDensity(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.VEIN_TOGGLE, router.veinToggle(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.VEIN_RIDGED, router.veinRidged(), modifiers),
            modifyDensityFunction(router, WrapNoiseRouterModifier.Target.VEIN_GAP, router.veinGap(), modifiers)
        ));

        return init.call(noiseSettings, noiseGetter, seed);
    }
}
