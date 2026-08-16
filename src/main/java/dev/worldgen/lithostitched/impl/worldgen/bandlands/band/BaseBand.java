package dev.worldgen.lithostitched.impl.worldgen.bandlands.band;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public record BaseBand(IntProvider count, IntProvider size, BlockState state) implements Band {
    public static final MapCodec<BaseBand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.codec(0, 256).fieldOf("count").forGetter(BaseBand::count),
        IntProvider.codec(1, 256).fieldOf("size").forGetter(BaseBand::size),
        BlockState.CODEC.fieldOf("state").forGetter(BaseBand::state)
    ).apply(instance, BaseBand::new));

    @Override
    public void fill(BlockState[] states, RandomSource random) {
        int count = this.count.sample(random);

        for(int i = 0; i < count; ++i) {
            int size = this.size.sample(random);
            int j = random.nextInt(states.length);

            for(int k = 0; j + k < states.length && k < size; ++k) {
                states[j + k] = state;
            }
        }
    }

    @Override
    public MapCodec<? extends Band> codec() {
        return CODEC;
    }
}
