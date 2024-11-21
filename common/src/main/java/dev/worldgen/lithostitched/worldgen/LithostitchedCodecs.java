package dev.worldgen.lithostitched.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Collection of Codecs used by Lithostitched.
 * @author Apollo
 */
public interface LithostitchedCodecs {
    Codec<HolderSet<Block>> BLOCK_SET = RegistryCodecs.homogeneousList(Registries.BLOCK);

    MapCodec<Float> CHANCE = Codec.floatRange(0.0F, 1.0F).fieldOf("chance");

    static <T> Codec<List<T>> singleOrList(Codec<T> codec) {
        return Codec.withAlternative(codec.listOf(), codec, List::of);
    }

    static <T> Codec<SimpleWeightedRandomList<T>> singleOrWeightedList(Codec<T> codec) {
        return Codec.withAlternative(SimpleWeightedRandomList.wrappedCodec(codec), codec, SimpleWeightedRandomList::single);
    }
}
