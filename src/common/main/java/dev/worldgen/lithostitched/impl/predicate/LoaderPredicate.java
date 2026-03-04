package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;

public record LoaderPredicate(String loader) implements LoadPredicate {
	public static final MapCodec<LoaderPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.STRING.fieldOf("loader").forGetter(LoaderPredicate::loader)
	).apply(instance, LoaderPredicate::new));
	
	@Override
	public boolean test() {
		return LithostitchedPlatform.getPlatformName().equals(this.loader);
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
