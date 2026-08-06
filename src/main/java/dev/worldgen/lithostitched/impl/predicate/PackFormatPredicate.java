package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.platform.LithostitchedVersion;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.InclusiveRange;

public record PackFormatPredicate(InclusiveRange<Integer> supportedFormats) implements LoadPredicate {
	public static final MapCodec<PackFormatPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		LithostitchedCodecs.INT_RANGE.fieldOf("supported_formats").forGetter(PackFormatPredicate::supportedFormats)
	).apply(instance, PackFormatPredicate::new));
	
	@Override
	public boolean test() {
		return this.supportedFormats.isValueInRange(LithostitchedVersion.getPackFormat());
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
