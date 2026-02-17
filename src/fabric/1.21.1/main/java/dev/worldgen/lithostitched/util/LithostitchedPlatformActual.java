package dev.worldgen.lithostitched.util;

import net.fabricmc.loader.api.FabricLoader;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedPlatformActual {
	@Actual
	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
