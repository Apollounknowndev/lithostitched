package dev.worldgen.lithostitched.util.weighted;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public final class WeightedList<E> {
    private final int totalWeight;
    private final List<Weighted<E>> items;
    @Nullable
    private final Selector<E> selector;

    WeightedList(List<? extends Weighted<E>> $$0) {
        this.items = List.copyOf($$0);
        this.totalWeight = getTotalWeight($$0, Weighted::weight);
        if (this.totalWeight == 0) {
            this.selector = null;
        } else if (this.totalWeight < 64) {
            this.selector = new Flat<>(this.items, this.totalWeight);
        } else {
            this.selector = new Compact<>(this.items);
        }

    }

    public static <T> int getTotalWeight(List<T> list, ToIntFunction<T> toIntFunction) {
        long l = 0L;

        T object;
        for(Iterator<T> var4 = list.iterator(); var4.hasNext(); l += toIntFunction.applyAsInt(object)) {
            object = var4.next();
        }

        if (l > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int)l;
        }
    }

    public static <E> WeightedList<E> of() {
        return new WeightedList<>(List.of());
    }

    public static <E> WeightedList<E> of(E entry) {
        return new WeightedList<>(List.of(new Weighted<>(entry, 1)));
    }

    @SafeVarargs
    public static <E> WeightedList<E> of(Weighted<E>... entries) {
        return new WeightedList<>(List.of(entries));
    }

    public static <E> WeightedList<E> of(List<Weighted<E>> entries) {
        return new WeightedList<>(entries);
    }

    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public <T> WeightedList<T> map(Function<E, T> $$0) {
        return new WeightedList<>(Lists.transform(this.items, ($$1) -> $$1.map($$0)));
    }

    public Optional<E> getRandom(RandomSource $$0) {
        if (this.selector == null) {
            return Optional.empty();
        } else {
            int $$1 = $$0.nextInt(this.totalWeight);
            return Optional.of(this.selector.get($$1));
        }
    }

    public E getRandomOrThrow(RandomSource $$0) {
        if (this.selector == null) {
            throw new IllegalStateException("Weighted list has no elements");
        } else {
            int $$1 = $$0.nextInt(this.totalWeight);
            return this.selector.get($$1);
        }
    }

    public List<Weighted<E>> unwrap() {
        return this.items;
    }

    public static <E> Codec<WeightedList<E>> codec(Codec<E> $$0) {
        return Weighted.codec($$0).listOf().xmap(WeightedList::of, WeightedList::unwrap);
    }

    public static <E> Codec<WeightedList<E>> codec(MapCodec<E> $$0) {
        return Weighted.codec($$0).listOf().xmap(WeightedList::of, WeightedList::unwrap);
    }

    public static <E> Codec<WeightedList<E>> nonEmptyCodec(Codec<E> $$0) {
        return ExtraCodecs.nonEmptyList(Weighted.codec($$0).listOf()).xmap(WeightedList::of, WeightedList::unwrap);
    }

    public static <E> Codec<WeightedList<E>> nonEmptyCodec(MapCodec<E> $$0) {
        return ExtraCodecs.nonEmptyList(Weighted.codec($$0).listOf()).xmap(WeightedList::of, WeightedList::unwrap);
    }

    public boolean contains(E $$0) {
        for(Weighted<E> $$1 : this.items) {
            if ($$1.value().equals($$0)) {
                return true;
            }
        }

        return false;
    }

    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        } else if (!(that instanceof WeightedList<?> thatList)) {
            return false;
        } else {
            return this.totalWeight == thatList.totalWeight && Objects.equals(this.items, thatList.items);
        }
    }

    public int hashCode() {
        int $$0 = this.totalWeight;
        $$0 = 31 * $$0 + this.items.hashCode();
        return $$0;
    }

    public static class Builder<E> {
        private final ImmutableList.Builder<Weighted<E>> result = ImmutableList.builder();

        public Builder() {
        }

        public Builder<E> add(E $$0) {
            return this.add($$0, 1);
        }

        public Builder<E> add(E $$0, int $$1) {
            this.result.add(new Weighted<>($$0, $$1));
            return this;
        }

        public WeightedList<E> build() {
            return new WeightedList<>(this.result.build());
        }
    }

    static class Flat<E> implements Selector<E> {
        private final Object[] entries;

        Flat(List<Weighted<E>> entries, int $$1) {
            this.entries = new Object[$$1];
            int $$2 = 0;

            for(Weighted<E> entry : entries) {
                int $$4 = entry.weight();
                Arrays.fill(this.entries, $$2, $$2 + $$4, entry.value());
                $$2 += $$4;
            }

        }

        public E get(int value) {
            return (E)this.entries[value];
        }
    }

    static class Compact<E> implements Selector<E> {
        private final Weighted[] entries;

        Compact(List<Weighted<E>> entries) {
            this.entries = entries.toArray(Weighted[]::new);
        }

        public E get(int value) {
            for(Weighted<?> entry : this.entries) {
                value -= entry.weight();
                if (value < 0) {
                    return (E)entry.value();
                }
            }

            throw new IllegalStateException(value + " exceeded total weight");
        }
    }

    interface Selector<E> {
        E get(int var1);
    }
}
