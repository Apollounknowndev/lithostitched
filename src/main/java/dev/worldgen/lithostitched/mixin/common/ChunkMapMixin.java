package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.worldgen.util.AquiferTarget;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.internal.ModifierManager;
import dev.worldgen.lithostitched.impl.worldgen.modifier.WrapAquifersModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.WrapNoiseRouterModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dev.worldgen.lithostitched.impl.worldgen.modifier.WrapAquifersModifier.modifyAquiferFunction;
import static dev.worldgen.lithostitched.impl.worldgen.modifier.WrapNoiseRouterModifier.modifyRouterFunction;

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
        List<WrapNoiseRouterModifier> routerModifiers = ModifierManager
            .getModifiersOfType(registryAccess, WrapNoiseRouterModifier.CODEC)
            .stream()
            .map(Map.Entry::getValue)
            .filter(modifier -> level.dimension().equals(modifier.dimension()))
            .toList();
        if (!routerModifiers.isEmpty()) {
            accessor.setNoiseRouter(new NoiseRouter(
                modifyRouterFunction(NoiseRouterTarget.TEMPERATURE, router.temperature(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.VEGETATION, router.vegetation(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.CONTINENTS, router.continents(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.EROSION, router.erosion(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.DEPTH, router.depth(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.RIDGES, router.ridges(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.CHUNK_SURFACE_LEVEL, router.chunkSurfaceLevel(), routerModifiers),
                modifyRouterFunction(NoiseRouterTarget.FINAL_DENSITY, router.finalDensity(), routerModifiers)
            ));
        }
        
        Aquifer.Config aquifers = settings.aquifers().orElse(null);
        if (aquifers != null) {
            List<WrapAquifersModifier> aquiferModifiers = ModifierManager
                .getModifiersOfType(registryAccess, WrapAquifersModifier.CODEC)
                .stream()
                .map(Map.Entry::getValue)
                .filter(modifier -> level.dimension().equals(modifier.dimension()))
                .toList();
            
            if (!aquiferModifiers.isEmpty()) {
                accessor.setAquifers(Optional.of(new Aquifer.Config(
                    modifyAquiferFunction(AquiferTarget.BARRIER, aquifers.barrierNoise(), aquiferModifiers),
                    modifyAquiferFunction(AquiferTarget.FLUID_LEVEL_FLOODEDNESS, aquifers.fluidLevelFloodednessNoise(), aquiferModifiers),
                    modifyAquiferFunction(AquiferTarget.FLUID_LEVEL_SPREAD, aquifers.fluidLevelSpreadNoise(), aquiferModifiers),
                    modifyAquiferFunction(AquiferTarget.LAVA, aquifers.lavaNoise(), aquiferModifiers),
                    modifyAquiferFunction(AquiferTarget.EXCLUSION, aquifers.exclusion(), aquiferModifiers),
                    modifyAquiferFunction(AquiferTarget.SURFACE_LEVEL, aquifers.surfaceLevel(), aquiferModifiers)
                )));
            }
        }

        return init.call(noises, seed, settings);
    }
}
