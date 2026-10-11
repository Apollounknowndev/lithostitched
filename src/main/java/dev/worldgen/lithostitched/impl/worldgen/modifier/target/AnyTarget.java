package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public record AnyTarget<T>(ResourceKey<Registry<T>> key) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return MapCodec.unit(() -> new AnyTarget<>(key));
		}
	};
	
	@Override
	public Iterable<Holder<T>> getHolders(RegistryAccess registries) {
		return registries.lookupOrThrow(this.key).listElements().map(t -> (Holder<T>) t).toList();
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
