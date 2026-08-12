package dev.worldgen.lithostitched.mixin.common.timeline;

import dev.worldgen.lithostitched.impl.duck.BiomeWeightingDuck;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeProbe.class)
public class EnvironmentAttributeProbeMixin {
	@Shadow
	@Final
	private SpatialAttributeInterpolator biomeInterpolator;
	
	@Inject(
		method = "tick",
		at = @At("HEAD")
	)
	private void clearTimelineWeights(Level level, Vec3 position, CallbackInfo ci) {
		BiomeWeightingDuck duck = (BiomeWeightingDuck) this.biomeInterpolator;
		duck.lithostitched$clear();
	}
	
	@Inject(
		method = "lambda$tick$0",
		at = @At("TAIL")
	)
	private void collectTimelineWeights(double weight, Holder<Biome> biome, CallbackInfo ci) {
		BiomeWeightingDuck duck = (BiomeWeightingDuck) this.biomeInterpolator;
		duck.lithostitched$accumulate(weight, biome);
	}
}
