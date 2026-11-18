package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.stream.Stream;

public record AnyOfTarget<T>(ResourceKey<Registry<T>> key, List<HolderTarget<T>> targets) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return HolderTarget.createCodec(key).listOf().fieldOf("targets").xmap(target -> new AnyOfTarget<>(key, target), AnyOfTarget::targets);
		}
	};
	
	@Override
	public Stream<Holder<T>> streamHolders(RegistryAccess registries) {
		return this.targets.stream().flatMap(target -> target.streamHolders(registries));
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
