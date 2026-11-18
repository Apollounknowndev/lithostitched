package dev.worldgen.lithostitched.api.worldgen.modifier.target;

import com.mojang.datafixers.util.Either;
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
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Interface representing a {@code HolderTarget}.
 * <p>
 * {@code HolderTarget}s are used in worldgen modifiers to allow targetting {@code Holder}s
 * by things such as the contents of their values rather than purely by being an entry in a list/tag.
 */
public interface HolderTarget<T> {
	Codec<HolderTarget<Biome>> BIOMES = createCodec(Registries.BIOME);
	
	Stream<Holder<T>> streamHolders(RegistryAccess registries);
	Type getType();
	
	default List<Holder<T>> getHolders(RegistryAccess registries) {
		return this.streamHolders(registries).distinct().toList();
	}
	
	default HolderSet<T> getHoldersAsSet(RegistryAccess registryAccess) {
		return HolderSet.direct(this.getHolders(registryAccess));
	}
	
	static <T> Codec<HolderTarget<T>> createCodec(ResourceKey<Registry<T>> key) {
		return Codec.either(
			LithostitchedBuiltInRegistries.HOLDER_TARGET_TYPE.byNameCodec().partialDispatch(
				"type",
				target -> DataResult.success(target.getType()),
				factory -> factory.apply(key)
			),
			RegistryCodecs.holderSet(key)
		).xmap(
			e -> e.map(Function.identity(), set -> new HolderSetTarget<>(key, set)),
			target -> target instanceof HolderSetTarget<T> setTarget ? Either.right(setTarget.set()) : Either.left(target)
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
