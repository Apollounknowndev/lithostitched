package dev.worldgen.lithostitched.mixin.server;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public final class DedicatedServerMixin {
	@Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V", shift = At.Shift.BEFORE), allow = 1)
	private void applyModdedBiomeSlices(CallbackInfoReturnable<Boolean> info) {
		Modifier.applyModifiers((MinecraftServer) (Object) this);
		SurfaceRuleManager.applySurfaceRules((MinecraftServer) (Object) this);
	}
}
