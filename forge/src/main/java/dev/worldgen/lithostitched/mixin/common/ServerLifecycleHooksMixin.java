package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.AbstractBiomeModifier;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.world.BiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(net.minecraftforge.server.ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {
    @Unique
    private static MinecraftServer serverInstance;
    @Inject(
        method = "runModifiers(Lnet/minecraft/server/MinecraftServer;)V",
        at = @At("HEAD"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        remap = false
    )
    private static void lithostitched$captureServer(MinecraftServer server, CallbackInfo ci) {
        serverInstance = server;
    }

    @ModifyArg(
        method = "lambda$runModifiers$4",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/world/ModifiableBiomeInfo;applyBiomeModifiers(Lnet/minecraft/core/Holder;Ljava/util/List;)V"
        ),
        index = 1,
        remap = false
    )
    private static List<BiomeModifier> lithostitched$injectBiomeModifers(List<BiomeModifier> biomeModifiers) {
        List<BiomeModifier> allBiomeModifiers = new ArrayList<>(biomeModifiers);
        var lithostitchedBiomeModifiers = serverInstance.registryAccess().registryOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER).entrySet().stream().filter((entry) -> entry.getValue() instanceof AbstractBiomeModifier).collect(Collectors.toSet());
        lithostitchedBiomeModifiers.forEach(
            (entry) -> {
                AbstractBiomeModifier modifier = ((AbstractBiomeModifier)entry.getValue());
                if (modifier.predicate().test()) {
                    allBiomeModifiers.add(modifier.forgeBiomeModifier());
                }
            }
        );
        return allBiomeModifiers;
    }

}
