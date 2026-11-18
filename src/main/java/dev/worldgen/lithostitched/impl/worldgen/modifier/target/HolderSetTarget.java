package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public record HolderSetTarget<T>(ResourceKey<Registry<T>> key, HolderSet<T> set) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return RegistryCodecs.holderSet(key).fieldOf("set").xmap(set -> new HolderSetTarget<>(key, set), HolderSetTarget::set);
		}
	};
	
	@Override
	public Stream<Holder<T>> streamHolders(RegistryAccess registries) {
		return this.set.stream();
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
