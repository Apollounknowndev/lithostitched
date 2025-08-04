package dev.worldgen.lithostitched.worldgen.bandlands.band;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public record RepeatingBand(IntProvider interval, IntProvider size, BlockState state) implements Band {
    public static final MapCodec<RepeatingBand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.POSITIVE_CODEC.fieldOf("interval").forGetter(RepeatingBand::interval),
        IntProvider.POSITIVE_CODEC.fieldOf("size").forGetter(RepeatingBand::size),
        BlockState.CODEC.fieldOf("state").forGetter(RepeatingBand::state)
    ).apply(instance, RepeatingBand::new));

    @Override
    public void fill(BlockState[] states, RandomSource random) {
        for(int i = 0; i < states.length; ++i) {
            i += interval.sample(random);
            for (int j = 0; j < size.sample(random); j++) {
                if (i + j < states.length) {
                    states[i + j] = state;
                }
            }
        }
    }

    @Override
    public MapCodec<? extends Band> codec() {
        return CODEC;
    }
}
