package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedRegistriesActual {
	@Actual
	public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.MODIFIER_TYPE).buildAndRegister();
}
