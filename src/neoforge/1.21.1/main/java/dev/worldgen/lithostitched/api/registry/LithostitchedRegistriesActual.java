package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import net.minecraft.core.Registry;
import net.msrandom.multiplatform.annotations.Actual;

import static dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries.DEFERRED_MODIFIER_TYPES;

public class LithostitchedRegistriesActual {
	@Actual
	public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = DEFERRED_MODIFIER_TYPES.makeRegistry(builder -> builder.sync(false));
}
