package dev.worldgen.lithostitched.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.util.WeightedList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.util.List;

/**
 * Collection of Codecs used by Lithostitched.
 * @author Apollo
 */
public interface LithostitchedCodecs {
    Codec<HolderSet<Block>> BLOCK_SET = RegistryCodecs.homogeneousList(Registries.BLOCK);
    MapCodec<Float> CHANCE = Codec.floatRange(0.0F, 1.0F).fieldOf("chance");
    Codec<InclusiveRange<Integer>> INT_RANGE = Codec.withAlternative(
        InclusiveRange.INT,
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("min_inclusive").orElse(Integer.MIN_VALUE).forGetter(InclusiveRange::minInclusive),
            Codec.INT.fieldOf("max_inclusive").orElse(Integer.MAX_VALUE).forGetter(InclusiveRange::maxInclusive)
        ).apply(instance, InclusiveRange::new))
    );
    Codec<InclusiveRange<Double>> DOUBLE_RANGE = Codec.withAlternative(
        InclusiveRange.codec(Codec.DOUBLE),
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min_inclusive").orElse(-Double.MAX_VALUE).forGetter(InclusiveRange::minInclusive),
            Codec.DOUBLE.fieldOf("max_inclusive").orElse(Double.MAX_VALUE).forGetter(InclusiveRange::maxInclusive)
        ).apply(instance, InclusiveRange::new))
    );
    Codec<DensityFunction> DF_BASE = DensityFunction.HOLDER_HELPER_CODEC;
    Codec<Holder<DensityFunction>> DF_REFERENCE = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC);

    static <T> MapCodec<HolderSet<T>> registrySet(ResourceKey<Registry<T>> key, String name) {
        return RegistryCodecs.homogeneousList(key).fieldOf(name);
    }

    static <T> Codec<List<T>> compactList(Codec<T> codec) {
        return Codec.withAlternative(codec.listOf(), codec, List::of);
    }

    static <T> Codec<WeightedList<T>> compactWeightedList(Codec<T> codec, boolean allowsEmpty) {
        Codec<WeightedList<T>> weightedCodec = allowsEmpty ? WeightedList.codec(codec) : WeightedList.nonEmptyCodec(codec);
        return Codec.withAlternative(weightedCodec, codec, WeightedList::of);
    }
}
