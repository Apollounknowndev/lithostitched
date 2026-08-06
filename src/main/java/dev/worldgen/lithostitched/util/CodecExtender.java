package dev.worldgen.lithostitched.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CodecExtender {
    public static <T> Codec<T> extend(Codec<T> original, BiFunction<RecordCodecBuilder.Instance<T>, RecordCodecBuilder<T, T>, ? extends App<RecordCodecBuilder.Mu<T>, T>> builder) {
        return Codec.lazyInitialized(
            () -> Codec.withAlternative(
                RecordCodecBuilder.create(
                    instance -> builder.apply(
                        instance,
                        MapCodec.assumeMapUnsafe(original).forGetter(Function.identity())
                    )
                ),
                original
            )
        );
    }

    public static <T> MapCodec<T> extend(MapCodec<T> original, BiFunction<RecordCodecBuilder.Instance<T>, RecordCodecBuilder<T, T>, ? extends App<RecordCodecBuilder.Mu<T>, T>> builder) {
        return Codec.mapEither(
            RecordCodecBuilder.mapCodec(
                (RecordCodecBuilder.Instance<T> instance) -> builder.apply(
                    instance,
                    original.forGetter(Function.identity())
                )
            ),
            original
        ).xmap(
            Either::unwrap,
            Either::left
        );
    }
}
