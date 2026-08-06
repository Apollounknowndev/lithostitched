package dev.worldgen.lithostitched.mixin.common.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Decoder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.impl.predicate.StubException;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(RegistryLoadTask.PendingRegistration.class)
public class PendingRegistrationMixin {
	//? if neoforge {
	@Inject(
		method = "loadFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"
		),
		cancellable = true
	)
	private static <T> void loadFromResource(Decoder<T> elementDecoder, RegistryOps<JsonElement> ops, ResourceKey<T> elementKey, Resource thunk, CallbackInfoReturnable<Either<T, Exception>> cir, @Local(name = "json") JsonElement json) {
		if (!json.isJsonObject()) return;
		JsonObject object = json.getAsJsonObject();
		if (!object.has("predicate")) return;
		if (LoadPredicate.CODEC.parse(ops, object.get("predicate")).result().orElse(LoadPredicate.alwaysTrue()).test()) return;
		cir.setReturnValue(Either.right(new StubException()));
	}
	//? }
}