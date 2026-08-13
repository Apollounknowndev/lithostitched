package dev.worldgen.lithostitched.mixin.common.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
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

import java.util.Optional;

@Debug(export = true)
@Mixin(RegistryLoadTask.PendingRegistration.class)
public class PendingRegistrationMixin {
	//? if fabric {
	@Inject(
		method = "loadFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"
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
	//? } else {
	/*@WrapOperation(
		method = "loadFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"
		)
	)
	private static <T> DataResult<T> handleLoadPredicate(Codec<T> codec, DynamicOps<JsonElement> ops, Object json, Operation<DataResult<T>> operation) {
		var candidate = operation.call(codec, ops, json);
		if (!(json instanceof JsonElement element && element.isJsonObject())) return candidate;
		
		JsonObject object = element.getAsJsonObject();
		if (!object.has("predicate")) return candidate;
		
		if (LoadPredicate.CODEC.parse(ops, object.get("predicate")).result().orElse(LoadPredicate.alwaysTrue()).test()) return candidate;
		
		return (DataResult<T>) DataResult.success(Optional.empty());
	}
	*///? }
}