package dev.worldgen.lithostitched.api.worldgen.bandlands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface Band {
    @SuppressWarnings("unchecked")
    Codec<Band> CODEC = Codec.lazyInitialized(() -> {
        var registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.BANDLANDS_BAND_TYPE.identifier());
        if (registry.isEmpty()) throw new NullPointerException("Bandlands band type registry does not exist yet!");
        return ((Registry<MapCodec<? extends Band>>) registry.get()).byNameCodec();
    }).dispatch(Band::codec, Function.identity());

    void fill(BlockState[] states, RandomSource random);
    MapCodec<? extends Band> codec();
}