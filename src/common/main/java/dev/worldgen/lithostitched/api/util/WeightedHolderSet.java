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
	private final Either<HolderSet<E>, WeightedList<Holder<E>>> set;
	private final Supplier<WeightedList<Holder<E>>> weightedList;
	
	public static <E> WeightedHolderSet<E> create(WeightedList<Holder<E>> set) {
		return new WeightedHolderSet<>(Either.right(set));
	}
	
	public static <E> WeightedHolderSet<E> create(HolderSet<E> set) {
		return new WeightedHolderSet<>(Either.left(set));
	}
	
	private WeightedHolderSet(Either<HolderSet<E>, WeightedList<Holder<E>>> set) {
		this.set = set;
		this.weightedList = Suppliers.memoize(() -> set.map(
			holders -> WeightedList.of(holders.stream().map(Weighted::new).toList()),
			t -> t
		));
	}
	
	public Optional<Holder<E>> getRandom(RandomSource random) {
		return this.weightedList.get().getRandom(random);
	}
	
	public static <E> Codec<WeightedHolderSet<E>> codec(Codec<HolderSet<E>> setCodec, Codec<Holder<E>> singleCodec) {
		return Codec.either(
			setCodec,
			WeightedList.codec(singleCodec)
		).xmap(WeightedHolderSet::new, set -> set.set);
	}
}
