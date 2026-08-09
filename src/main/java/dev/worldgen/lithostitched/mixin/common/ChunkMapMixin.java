package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
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
            target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/core/HolderGetter;JLnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;)Lnet/minecraft/world/level/levelgen/RandomState;"
        )
    )
    private RandomState wrapNoiseRouter(HolderGetter<NormalNoise> noises, long seed, NoiseGeneratorSettings settings, Operation<RandomState> init, ServerLevel level, @Local(name = "registryAccess") RegistryAccess registryAccess) {
        NoiseGeneratorSettingsAccessor accessor = ((NoiseGeneratorSettingsAccessor)(Object) settings);
        NoiseRouter router = settings.noiseRouter();

        List<WrapNoiseRouterModifier> modifiers = ModifierManager
            .getModifiersOfType(registryAccess, WrapNoiseRouterModifier.CODEC)
            .stream()
            .map(Map.Entry::getValue)
            .filter(modifier -> level.dimension().equals(modifier.dimension()))
            .toList();

        if (!modifiers.isEmpty()) {
            accessor.setNoiseRouter(new NoiseRouter(
                modifyDensityFunction(NoiseRouterTarget.TEMPERATURE, router.temperature(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.VEGETATION, router.vegetation(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.CONTINENTS, router.continents(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.EROSION, router.erosion(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.DEPTH, router.depth(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.RIDGES, router.ridges(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.PRELIMINARY_SURFACE_LEVEL, router.preliminarySurfaceLevel(), modifiers),
                modifyDensityFunction(NoiseRouterTarget.FINAL_DENSITY, router.finalDensity(), modifiers)
            ));
        }

        return init.call(noises, seed, settings);
    }
}
