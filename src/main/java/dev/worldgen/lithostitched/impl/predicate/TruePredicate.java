package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;

public record TruePredicate() implements LoadPredicate {
	public static final MapCodec<TruePredicate> CODEC = MapCodec.unit(TruePredicate::new);
	
	@Override
	public boolean test() {
		return true;
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
