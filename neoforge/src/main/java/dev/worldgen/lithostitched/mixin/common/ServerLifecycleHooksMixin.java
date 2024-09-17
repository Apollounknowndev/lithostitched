package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.AbstractBiomeModifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.world.BiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = net.neoforged.neoforge.server.ServerLifecycleHooks.class, remap = false)
public class ServerLifecycleHooksMixin {

    @ModifyVariable(
        method = "runModifiers",
        at = @At("STORE"),
        ordinal = 0
    )
    private static List<BiomeModifier> lithostitched$injectBiomeModifers(List<BiomeModifier> biomeModifiers, MinecraftServer server) {
        List<BiomeModifier> allBiomeModifiers = new ArrayList<>(biomeModifiers);

        var lithostitchedBiomeModifiers = server.registryAccess().registryOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER).entrySet().stream().filter(entry -> entry.getValue() instanceof AbstractBiomeModifier).collect(Collectors.toSet());
        lithostitchedBiomeModifiers.forEach(
            entry -> {
                AbstractBiomeModifier modifier = ((AbstractBiomeModifier)entry.getValue());
                allBiomeModifiers.add(modifier.neoforgeBiomeModifier());
            }
        );

        return allBiomeModifiers;
    }
}
