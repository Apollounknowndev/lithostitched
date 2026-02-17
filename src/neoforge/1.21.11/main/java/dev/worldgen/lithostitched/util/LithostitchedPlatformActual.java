package dev.worldgen.lithostitched.util;

import net.msrandom.multiplatform.annotations.Actual;
import net.neoforged.fml.ModList;

public class LithostitchedPlatformActual {
	@Actual
	public static boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}
}
