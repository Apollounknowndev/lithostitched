package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.worldgen.modifier.NeoforgeModifierHolder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.world.BiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = net.neoforged.neoforge.server.ServerLifecycleHooks.class, remap = false)
public class ServerLifecycleHooksMixin {

    @ModifyVariable(
        method = "runModifiers",
        at = @At("STORE"),
        ordinal = 0
    )
    private static List<BiomeModifier> lithostitched$injectBiomeModifers(List<BiomeModifier> biomeModifiers, MinecraftServer server) {
        List<BiomeModifier> allBiomeModifiers = new ArrayList<>(biomeModifiers);
        
        var lithostitchedBiomeModifiers = server.registryAccess().lookupOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER)
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() instanceof NeoforgeModifierHolder)
            .toList()
            ;
        lithostitchedBiomeModifiers.forEach(
            entry -> {
                NeoforgeModifierHolder modifier = ((NeoforgeModifierHolder)entry.getValue());
                allBiomeModifiers.add(modifier.createNeoforgeModifier());
            }
        );

        return allBiomeModifiers;
    }
}
