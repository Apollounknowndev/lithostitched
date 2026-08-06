package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;

import java.util.List;

public record NotPredicate(LoadPredicate predicate) implements LoadPredicate {
	public static final MapCodec<NotPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		LoadPredicate.CODEC.fieldOf("predicate").forGetter(NotPredicate::predicate)
	).apply(instance, NotPredicate::new));
	
	@Override
	public boolean test() {
		return !this.predicate.test();
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
