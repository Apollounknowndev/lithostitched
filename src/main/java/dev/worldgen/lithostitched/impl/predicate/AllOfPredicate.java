package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;

import java.util.List;

public record AllOfPredicate(List<LoadPredicate> predicates) implements LoadPredicate {
	public static final MapCodec<AllOfPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		LoadPredicate.CODEC.listOf().fieldOf("predicates").forGetter(AllOfPredicate::predicates)
	).apply(instance, AllOfPredicate::new));
	
	@Override
	public boolean test() {
		for (LoadPredicate predicate : this.predicates()) {
			if (!predicate.test()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
