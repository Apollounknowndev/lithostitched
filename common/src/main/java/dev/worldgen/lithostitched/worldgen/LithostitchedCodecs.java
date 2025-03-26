package dev.worldgen.lithostitched.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;

import static net.minecraft.util.random.WeightedList.*;

/**
 * Collection of Codecs used by Lithostitched.
 * @author Apollo
 */
public interface LithostitchedCodecs {
    Codec<HolderSet<Block>> BLOCK_SET = RegistryCodecs.homogeneousList(Registries.BLOCK);
    MapCodec<Float> CHANCE = Codec.floatRange(0.0F, 1.0F).fieldOf("chance");

    static <T> MapCodec<HolderSet<T>> registrySet(ResourceKey<Registry<T>> registry, String name) {
        return RegistryCodecs.homogeneousList(registry).fieldOf(name);
    }

    static <T> Codec<WeightedList<T>> singleOrWeightedList(Codec<T> codec, boolean allowsEmpty) {
        Codec<WeightedList<T>> weightedListCodec = allowsEmpty ? codec(codec) : nonEmptyCodec(codec);
        return Codec.withAlternative(weightedListCodec, codec, WeightedList::of);
    }
}
