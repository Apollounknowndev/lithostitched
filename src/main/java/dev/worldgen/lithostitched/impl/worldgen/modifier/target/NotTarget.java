package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public record NotTarget<T>(ResourceKey<Registry<T>> key, HolderTarget<T> target) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return HolderTarget.createCodec(key).fieldOf("target").xmap(target -> new NotTarget<>(key, target), NotTarget::target);
		}
	};
	
	@Override
	public Stream<Holder<T>> streamHolders(RegistryAccess registries) {
		return registries.lookupOrThrow(this.key).listElements().filter(holder -> !this.target.getHolders(registries).contains(holder)).map(t -> t);
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
