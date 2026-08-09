package dev.worldgen.lithostitched.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.impl.duck.StructureAttributesHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
	@Shadow private float currentGain;
	
	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;getMusicVolume()F"
		)
	)
	private float applyVolumeMultiplier(Minecraft minecraft, Operation<Float> operation) {
		float baseVolume = operation.call(minecraft);
		if (minecraft.level == null) return baseVolume;
		return StructureAttributesHolder.from(minecraft.level).shouldFadeMusic() ? -1 : baseVolume;
	}
}
