package dev.worldgen.lithostitched.mixin.client;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public final class IntegratedServerMixin {
	@Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V", shift = At.Shift.BEFORE))
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		Modifier.applyModifiers((MinecraftServer) (Object) this);
		SurfaceRuleManager.applySurfaceRules((MinecraftServer) (Object) this);
	}
}
