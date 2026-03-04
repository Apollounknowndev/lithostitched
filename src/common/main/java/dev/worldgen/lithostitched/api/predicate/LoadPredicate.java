package dev.worldgen.lithostitched.api.predicate;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.impl.predicate.*;
import net.minecraft.util.InclusiveRange;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface LoadPredicate {
	Codec<LoadPredicate> CODEC = LithostitchedBuiltInRegistries.LOAD_PREDICATE_TYPE.byNameCodec().dispatch(LoadPredicate::codec, Function.identity());
	MapCodec<Optional<LoadPredicate>> FIELD_CODEC = LoadPredicate.CODEC.optionalFieldOf("predicate");
	
	boolean test();
	MapCodec<? extends LoadPredicate> codec();
	
	// Factory methods for built-in load predicates.
	
	static LoadPredicate allOf(LoadPredicate... predicates) {
		return new AllOfPredicate(List.of(predicates));
	}
	
	static LoadPredicate anyOf(LoadPredicate... predicates) {
		return new AnyOfPredicate(List.of(predicates));
	}
	
	static LoadPredicate isFabric() {
		return new LoaderPredicate("fabric");
	}
	
	static LoadPredicate isNeoforge() {
		return new LoaderPredicate("neoforge");
	}
	
	static LoadPredicate modLoaded(String modId) {
		return new ModLoadedPredicate(modId);
	}
	
	static LoadPredicate not(LoadPredicate predicate) {
		return new NotPredicate(predicate);
	}
	
	static LoadPredicate packFormat(InclusiveRange<Integer> supportedFormats) {
		return new PackFormatPredicate(supportedFormats);
	}
	
	static LoadPredicate alwaysTrue() {
		return new TruePredicate();
	}
}
