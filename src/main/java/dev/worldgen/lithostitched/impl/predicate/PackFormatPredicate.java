package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import net.minecraft.util.valueproviders.IntProvider;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.InclusiveRange;

public record PackFormatPredicate(InclusiveRange<Integer> supportedFormats) implements LoadPredicate {
	public static final MapCodec<PackFormatPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		LithostitchedCodecs.INT_RANGE.fieldOf("supported_formats").forGetter(PackFormatPredicate::supportedFormats)
	).apply(instance, PackFormatPredicate::new));
	
	@Override
	public boolean test() {
		return this.supportedFormats.isValueInRange(
			SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)
		);
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
