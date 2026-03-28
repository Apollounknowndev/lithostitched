package dev.worldgen.lithostitched.mixin.common.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
	@Inject(
		method = "loadElementFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"
		),
		cancellable = true
	)
	private static <E> void applyLoadPredicates(WritableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, ResourceKey<E> key, Resource resource, RegistrationInfo registrationInfo, CallbackInfo ci, @Local(ordinal = 0) JsonElement element) {
		if (!element.isJsonObject()) return;
		JsonObject object = element.getAsJsonObject();
		if (!object.has("predicate")) return;
		if (LoadPredicate.CODEC.parse(ops, object.get("predicate")).result().orElse(LoadPredicate.alwaysTrue()).test()) return;
		ci.cancel();
	}
}
