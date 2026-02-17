package dev.worldgen.lithostitched;

import net.fabricmc.loader.api.FabricLoader;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedActual {
	@Actual
	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
