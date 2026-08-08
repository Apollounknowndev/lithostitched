package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;

public record LoaderPredicate(String loader) implements LoadPredicate {
	public static final MapCodec<LoaderPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.STRING.fieldOf("loader").forGetter(LoaderPredicate::loader)
	).apply(instance, LoaderPredicate::new));
	
	//? if fabric {
	public static final String CURRENT_LOADER = "fabric";
	//? } else {
	/*public static final String CURRENT_LOADER = "neoforge";
	*///? }
	
	@Override
	public boolean test() {
		return CURRENT_LOADER.equals(this.loader);
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
