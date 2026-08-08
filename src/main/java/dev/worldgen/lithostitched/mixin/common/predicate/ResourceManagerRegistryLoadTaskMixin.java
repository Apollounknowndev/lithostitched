package dev.worldgen.lithostitched.mixin.common.predicate;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.worldgen.lithostitched.impl.predicate.StubException;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ResourceManagerRegistryLoadTask.class, priority = 500)
public class ResourceManagerRegistryLoadTaskMixin {
	//? if fabric {
	@ModifyExpressionValue(method = "lambda$load$2", at = @At(value = "NEW", target = "net/minecraft/resources/RegistryLoadTask$PendingRegistration"))
	private RegistryLoadTask.PendingRegistration<?> load(RegistryLoadTask.PendingRegistration<?> original) {
		if (original == null || (original.value().right().isPresent() && original.value().right().get() instanceof StubException)) {
			return null;
		}
		
		return original;
	}
	//? }
}
