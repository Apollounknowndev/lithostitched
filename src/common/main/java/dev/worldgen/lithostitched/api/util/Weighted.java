package dev.worldgen.lithostitched.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

public record Weighted<T>(T value, int weight) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public Weighted(T value, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight should be >= 0");
        } else {
            if (weight == 0 && SharedConstants.IS_RUNNING_IN_IDE) {
                LOGGER.warn("Found 0 weight, make sure this is intentional!");
            }

            this.value = value;
            this.weight = weight;
        }
    }

    public static <E> Codec<Weighted<E>> codec(Codec<E> codec) {
        return codec(codec.fieldOf("data"));
    }

    public static <E> Codec<Weighted<E>> codec(MapCodec<E> mapCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
            mapCodec.forGetter(Weighted::value),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(Weighted::weight)
        ).apply(instance, (Weighted::new)));
    }

    public <U> Weighted<U> map(Function<T, U> function) {
        return new Weighted<>(function.apply(this.value()), this.weight);
    }

    public T value() {
        return this.value;
    }

    public int weight() {
        return this.weight;
    }
}
