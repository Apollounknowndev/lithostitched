package dev.worldgen.lithostitched.impl.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.impl.platform.LithostitchedPlatform;

public record ModLoadedPredicate(String modId) implements LoadPredicate {
	public static final MapCodec<ModLoadedPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.STRING.fieldOf("mod_id").forGetter(ModLoadedPredicate::modId)
	).apply(instance, ModLoadedPredicate::new));
	
	@Override
	public boolean test() {
		return LithostitchedPlatform.isModLoaded(modId);
	}
	
	@Override
	public MapCodec<? extends LoadPredicate> codec() {
		return CODEC;
	}
}
