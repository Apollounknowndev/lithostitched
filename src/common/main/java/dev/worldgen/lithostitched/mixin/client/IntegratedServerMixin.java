package dev.worldgen.lithostitched.mixin.client;

import dev.worldgen.lithostitched.worldgen.LithostitchedEvents;
import dev.worldgen.lithostitched.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Priority of 1500 to apply biome injectors after Blueprint
@Mixin(value = IntegratedServer.class, priority = 1500)
public final class IntegratedServerMixin {
	@Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V", shift = At.Shift.BEFORE))
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		LithostitchedEvents.onServerAboutToStart((MinecraftServer) (Object) this);
	}
}
