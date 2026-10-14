package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;

public record HolderSetTarget<T>(HolderSet<T> set, ResourceKey<Registry<T>> key) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return RegistryCodecs.homogeneousList(key).fieldOf("set").xmap(set -> new HolderSetTarget<>(set, key), HolderSetTarget::set);
		}
	};
	
	@Override
	public Iterable<Holder<T>> getHolders(RegistryAccess registries) {
		return this.set;
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
