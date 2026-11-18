package dev.worldgen.lithostitched.impl.worldgen.modifier.target;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record AllOfTarget<T>(ResourceKey<Registry<T>> key, List<HolderTarget<T>> targets) implements HolderTarget<T> {
	public static final Type TYPE = new SimpleType() {
		@Override
		public <A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key) {
			return HolderTarget.createCodec(key).listOf().fieldOf("targets").xmap(target -> new AllOfTarget<>(key, target), AllOfTarget::targets);
		}
	};
	
	@Override
	public Stream<Holder<T>> streamHolders(RegistryAccess registries) {
		if (this.targets.isEmpty()) return Stream.of();
		if (this.targets.size() == 1) return this.targets.getFirst().streamHolders(registries);
		
		Stream<Holder<T>> totalHolders = this.targets.getFirst().streamHolders(registries);
		
		for (int i = 1; i < this.targets.size(); i++) {
			int index = i;
			totalHolders = totalHolders.filter(holder -> this.targets.get(index).getHolders(registries).contains(holder));
		}
		
		return totalHolders;
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
