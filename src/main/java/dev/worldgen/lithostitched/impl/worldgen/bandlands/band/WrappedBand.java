package dev.worldgen.lithostitched.impl.worldgen.bandlands.band;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public record WrappedBand(IntProvider interval, IntProvider maxCount, float wrapperChance, BlockState wrapperState, BlockState wrappedState) implements Band {
    public static final MapCodec<WrappedBand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProviders.codec(1, 256).fieldOf("interval").forGetter(WrappedBand::interval),
        IntProviders.codec(1, 256).fieldOf("max_count").forGetter(WrappedBand::maxCount),
        Codec.floatRange(0, 1).fieldOf("wrapper_chance").forGetter(WrappedBand::wrapperChance),
        BlockState.CODEC.fieldOf("wrapper_state").forGetter(WrappedBand::wrapperState),
        BlockState.CODEC.fieldOf("wrapped_state").forGetter(WrappedBand::wrappedState)
    ).apply(instance, WrappedBand::new));

    @Override
    public void fill(BlockState[] states, RandomSource random) {
        int count = 0;
        int maxCount = this.maxCount.sample(random);
        for(int i = 0; count < maxCount && i < states.length; i += interval.sample(random)) {
            states[i] = wrappedState;
            if (i - 1 > 0 && random.nextFloat() < wrapperChance) {
                states[i - 1] = wrapperState;
            }

            if (i + 1 < states.length && random.nextFloat() < wrapperChance) {
                states[i + 1] = wrapperState;
            }

            ++count;
        }
    }

    @Override
    public MapCodec<? extends Band> codec() {
        return CODEC;
    }
}
