package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;

import java.util.List;

public record AnyOfPredicate(List<LoadPredicate> predicates) implements LoadPredicate {
	public static final MapCodec<AnyOfPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		LoadPredicate.CODEC.listOf().fieldOf("predicates").forGetter(AnyOfPredicate::predicates)
	).apply(instance, AnyOfPredicate::new));
	
	@Override
	public boolean test() {
		for (LoadPredicate predicate : this.predicates()) {
			if (predicate.test()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
