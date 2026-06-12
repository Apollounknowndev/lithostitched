package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.impl.worldgen.modifier.NeoforgeModifierHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.world.BiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = net.neoforged.neoforge.server.ServerLifecycleHooks.class, remap = false)
public class ServerLifecycleHooksMixin {

    @ModifyVariable(
        method = "runModifiers",
        at = @At("STORE"),
        ordinal = 0
    )
    private static List<BiomeModifier> lithostitched$injectBiomeModifers(List<BiomeModifier> biomeModifiers, MinecraftServer server) {
        List<BiomeModifier> allBiomeModifiers = new ArrayList<>(biomeModifiers);
        
        Map<Identifier, WorldgenModifier> modifiers = ModifierManager.getAllModifiers(server.registryAccess());
        var lithostitchedBiomeModifiers = modifiers.entrySet().stream().filter(entry -> entry.getValue() instanceof NeoforgeModifierHolder).map(entry -> Map.entry(entry.getKey(), (NeoforgeModifierHolder) entry.getValue())).toList();
        lithostitchedBiomeModifiers.forEach(entry -> allBiomeModifiers.add(entry.getValue().createNeoforgeModifier()));

        return allBiomeModifiers;
    }
}
