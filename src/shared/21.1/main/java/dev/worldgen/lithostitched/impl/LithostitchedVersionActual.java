package dev.worldgen.lithostitched.impl;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.msrandom.multiplatform.annotations.Actual;
import net.msrandom.multiplatform.annotations.Expect;

public class LithostitchedVersionActual {
	@Actual
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
	}
	
	@Actual
	public static void initVersionRegistrations() {
	
	}
}
