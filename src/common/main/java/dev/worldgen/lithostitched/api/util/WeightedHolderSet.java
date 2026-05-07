package dev.worldgen.lithostitched.api.util;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;

import java.util.Optional;
import java.util.function.Supplier;

public class WeightedHolderSet<E> {
	private final Either<WeightedList<Holder<E>>, HolderSet<E>> set;
	private final Supplier<WeightedList<Holder<E>>> weightedList;
	
	public static <E> WeightedHolderSet<E> create(WeightedList<Holder<E>> set) {
		return new WeightedHolderSet<>(Either.left(set));
	}
	
	public static <E> WeightedHolderSet<E> create(HolderSet<E> set) {
		return new WeightedHolderSet<>(Either.right(set));
	}
	
	private WeightedHolderSet(Either<WeightedList<Holder<E>>, HolderSet<E>> set) {
		this.set = set;
		this.weightedList = Suppliers.memoize(() -> set.map(
			t -> t,
			holders -> WeightedList.of(holders.stream().map(Weighted::new).toList())
		));
	}
	
	public Optional<Holder<E>> getRandom(RandomSource random) {
		return this.weightedList.get().getRandom(random);
	}
	
	public static <E> Codec<WeightedHolderSet<E>> codec(Codec<Holder<E>> singleCodec, Codec<HolderSet<E>> setCodec) {
		return Codec.either(
			WeightedList.codec(singleCodec),
			setCodec
		).xmap(WeightedHolderSet::new, set -> set.set);
	}
}
