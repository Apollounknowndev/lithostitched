package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier.Target;
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
    private RandomState init(NoiseGeneratorSettings noiseSettings, HolderGetter<NormalNoise.NoiseParameters> noiseGetter, long seed, Operation<RandomState> init, @Local(ordinal = 0) RegistryAccess registries) {
        NoiseGeneratorSettingsAccessor accessor = ((NoiseGeneratorSettingsAccessor)(Object)noiseSettings);
        NoiseRouter router = noiseSettings.noiseRouter();
        Registry<Modifier> modifiers = registries.registryOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);
        accessor.setNoiseRouter(new NoiseRouter(
            modifyDensityFunction(Target.BARRIER, router.barrierNoise(), modifiers),
            modifyDensityFunction(Target.FLUID_LEVEL_FLOODEDNESS, router.fluidLevelFloodednessNoise(), modifiers),
            modifyDensityFunction(Target.FLUID_LEVEL_SPREAD, router.fluidLevelSpreadNoise(), modifiers),
            modifyDensityFunction(Target.LAVA, router.lavaNoise(), modifiers),
            modifyDensityFunction(Target.TEMPERATURE, router.temperature(), modifiers),
            modifyDensityFunction(Target.VEGETATION, router.vegetation(), modifiers),
            modifyDensityFunction(Target.CONTINENTS, router.continents(), modifiers),
            modifyDensityFunction(Target.EROSION, router.erosion(), modifiers),
            modifyDensityFunction(Target.DEPTH, router.depth(), modifiers),
            modifyDensityFunction(Target.RIDGES, router.ridges(), modifiers),
            modifyDensityFunction(Target.INITIAL_DENSITY, router.initialDensityWithoutJaggedness(), modifiers),
            modifyDensityFunction(Target.FINAL_DENSITY, router.finalDensity(), modifiers),
            modifyDensityFunction(Target.VEIN_TOGGLE, router.veinToggle(), modifiers),
            modifyDensityFunction(Target.VEIN_RIDGED, router.veinRidged(), modifiers),
            modifyDensityFunction(Target.VEIN_GAP, router.veinGap(), modifiers)
        ));

        return init.call(noiseSettings, noiseGetter, seed);
    }
}
