package dev.worldgen.lithostitched.api.worldgen.modifier.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.impl.worldgen.modifier.target.HolderSetTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface representing a {@code HolderTarget}.
 * <p>
 * {@code HolderTarget}s are used in worldgen modifiers to allow targetting {@code Holder}s
 * by things such as the contents of their values rather than purely by being an entry in a list/tag.
 */
public interface HolderTarget<T> {
	Codec<HolderTarget<Biome>> BIOMES = createCodec(Registries.BIOME);
	
	Iterable<Holder<T>> getHolders(RegistryAccess registries);
	Type getType();
	
	default HolderSet<T> getHoldersAsSet(RegistryAccess registryAccess) {
		List<Holder<T>> holders = new ArrayList<>();
		for (Holder<T> holder : this.getHolders(registryAccess)) {
			holders.add(holder);
		}
		return HolderSet.direct(holders);
	}
	
	static <T> HolderTarget<T> of(Holder<T> holder) {
		return new HolderSetTarget<>(HolderSet.direct(holder), holder.unwrapKey().orElseThrow().registryKey());
	}
	
	static <T> HolderTarget<T> of(HolderSet<T> holderSet) {
		return new HolderSetTarget<>(holderSet, holderSet.get(0).unwrapKey().orElseThrow().registryKey());
	}
	
	static <T> Codec<HolderTarget<T>> createCodec(ResourceKey<Registry<T>> key) {
		return Codec.withAlternative(
			LithostitchedBuiltInRegistries.HOLDER_TARGET_TYPE.byNameCodec().partialDispatch(
				"type",
				target -> DataResult.success(target.getType()),
				factory -> factory.apply(key)
			),
			RegistryCodecs.homogeneousList(key),
			set -> new HolderSetTarget<>(set, key)
		);
	}
	
	interface Type {
		<A> DataResult<MapCodec<? extends HolderTarget<A>>> apply(ResourceKey<Registry<A>> key);
	}
	
	interface SimpleType extends Type {
		default <A> DataResult<MapCodec<? extends HolderTarget<A>>> apply(ResourceKey<Registry<A>> key) {
			return DataResult.success(this.applySimple(key));
		}
		
		<A> MapCodec<? extends HolderTarget<A>> applySimple(ResourceKey<Registry<A>> key);
	}
}
