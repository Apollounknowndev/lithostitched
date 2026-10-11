package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.regex.Pattern;

public record RegexTarget<T>(ResourceKey<Registry<T>> key, String regex) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return ExtraCodecs.PATTERN.fieldOf("pattern").xmap(pattern -> new RegexTarget<>(key, pattern.pattern()), target -> Pattern.compile(target.regex));
		}
	};
	
	@Override
	public Iterable<Holder<T>> getHolders(RegistryAccess registries) {
		return registries.lookupOrThrow(this.key).listElements().filter(t -> t.key().identifier().toString().matches(this.regex)).map(t -> (Holder<T>) t).toList();
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
