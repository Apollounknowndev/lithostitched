package dev.worldgen.lithostitched.impl;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.msrandom.multiplatform.annotations.Expect;

public class LithostitchedVersion {
	@Expect
	public static int getPackFormat();
	
	@Expect
	public static void initVersionRegistrations();
	
	@Expect
	public static Codec<FloatProvider> floatProviderCodec(float min, float max);
	
	@Expect
	public static Codec<IntProvider> intProviderCodec(int min, int max);
	
	@Expect
	public static IntProvider uniformInt(int min, int max);
	
	@Expect
	public static IntProvider constantInt(int value);
}
