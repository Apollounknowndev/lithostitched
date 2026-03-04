package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.ClocheHacks;
import net.msrandom.multiplatform.annotations.Actual;

public class DelegatingConfigActual {
	@Actual
	public static MapCodec<DelegatingConfig> getCodec() {
		return ClocheHacks.DELEGATING_CONFIG_CODEC;
	}
}
