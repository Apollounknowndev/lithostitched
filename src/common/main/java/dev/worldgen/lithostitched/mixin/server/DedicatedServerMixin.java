package dev.worldgen.lithostitched.mixin.server;

import dev.worldgen.lithostitched.worldgen.biomeinjector.internal.BiomeInjectorManager;
import dev.worldgen.lithostitched.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Priority of 1500 to apply biome injectors after Blueprint
@Mixin(value = DedicatedServer.class, priority = 1500)
public final class DedicatedServerMixin {
	@Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V", shift = At.Shift.BEFORE), allow = 1)
	private void initServer(CallbackInfoReturnable<Boolean> info) {
		ModifierManager.applyModifiers((MinecraftServer) (Object) this);
		SurfaceRuleManager.applySurfaceRules((MinecraftServer) (Object) this);
		BiomeInjectorManager.applyBiomeInjectors((MinecraftServer) (Object) this);
	}
}
