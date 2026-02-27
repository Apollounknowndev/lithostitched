package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import net.minecraft.core.Registry;
import net.msrandom.multiplatform.annotations.Expect;

/**
 * All of Lithostitched's static registries.
 */
public class LithostitchedRegistries {
	@Expect
	public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE;
	
	/**
	 * Purely for use in Lithostitched, don't call this.
	 */
	public static void init() {
	
	}
}
