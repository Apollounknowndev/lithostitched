package dev.worldgen.lithostitched.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.NoOpModifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

/**
 * Hacky/brittle code to make modifier predicates work on 1.20.1.
 * Ideally, I don't have to edit this code again.
 */
@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
	@Unique
	@Final
	private static final JsonObject lithostitched$blankModifier = new JsonObject();

	@Unique
	private static boolean lithostitched$useBlankModifier = false;

	@Inject(
		method = "loadRegistryContents",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
			shift = At.Shift.BEFORE,
			remap = false
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static <E> void parseModifierPredicate(RegistryOps.RegistryInfoLookup $$0, ResourceManager $$1, ResourceKey<? extends Registry<E>> key, WritableRegistry<E> $$3, Decoder<E> $$4, Map<ResourceKey<?>, Exception> $$5, CallbackInfo ci, String $$6, FileToIdConverter $$7, RegistryOps<JsonElement> ops, Iterator<Map.Entry<ResourceLocation, Resource>> var9, Map.Entry<ResourceLocation, Resource> $$9, ResourceLocation $$10, ResourceKey<E> $$11, Resource $$12, Reader $$13, JsonElement json) {
		lithostitched$useBlankModifier = false;

		if (key.equals(LithostitchedRegistryKeys.WORLDGEN_MODIFIER)) {
			// This shouldn't be able to fail(?)
			Modifier modifier = NoOpModifier.CODEC.parse(ops, json).result().get();
			if (!modifier.getPredicate().test()) {
				lithostitched$useBlankModifier = true;
			}
		}
	}

	@ModifyArg(
		method = "loadRegistryContents",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
			remap = false
		),
		index = 1
	)
	private static <T> T useBlankModifier(T t) {
		if (lithostitched$useBlankModifier) {
			return (T) lithostitched$blankModifier;
		}
		return t;
	}

	static {
		JsonObject predicate = new JsonObject();
		predicate.addProperty("type", "lithostitched:true");

		lithostitched$blankModifier.addProperty("type", "lithostitched:no_op");
		lithostitched$blankModifier.add("predicate", predicate);
	}
}
