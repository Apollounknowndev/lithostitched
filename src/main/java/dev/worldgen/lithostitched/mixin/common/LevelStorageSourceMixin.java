package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
	@WrapOperation(
		method = "readExistingSavedData",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/nbt/NbtAccounter;defaultQuota()Lnet/minecraft/nbt/NbtAccounter;"
		)
	)
	private static NbtAccounter backportLevelUpdateBiomeSourceFix(Operation<NbtAccounter> original) {
		return NbtAccounter.unlimitedHeap();
	}
}