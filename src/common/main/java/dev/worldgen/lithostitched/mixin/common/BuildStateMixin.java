package dev.worldgen.lithostitched.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bypasses validation for keys that use the "minecraft" namespace.
 * Required for compatibility with VanillaBackport as the mod
 * spoofs the vanilla namespace for backported content.
 * 
 * @author sunshinekitsune
 */
@Mixin(targets = "net.minecraft.core.RegistrySetBuilder$BuildState")
public abstract class BuildStateMixin {
    @Inject(method = "reportNotCollectedHolders", at = @At("HEAD"), cancellable = true)
    private void lithostitched$whitelistBackportedVanillaKeys(CallbackInfo ci) {
        ci.cancel();
    }
}