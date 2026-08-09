package dev.worldgen.lithostitched.impl.worldgen.bandlands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.duck.SurfaceSystemAccessor;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceSystem;

import java.util.Arrays;
import java.util.List;

public final class Bandlands {
    public static final Codec<Bandlands> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockState.CODEC.fieldOf("base").forGetter(Bandlands::base),
        Band.CODEC.listOf().fieldOf("bands").forGetter(Bandlands::bands)
    ).apply(instance, Bandlands::new));
    private final BlockState base;
    private final List<Band> bands;
    private BlockState[] filledBandlands;

    public Bandlands(BlockState base, List<Band> bands) {
        this.base = base;
        this.bands = bands;
    }

    public BlockState base() {
        return base;
    }

    public List<Band> bands() {
        return bands;
    }

    public BlockState getBand(SurfaceSystem system, int x, int y, int z) {
        if (filledBandlands == null) return base;

        int i = (int)Math.round(((SurfaceSystemAccessor)system).getBandOffsetNoise().get(x, 0.0F, z) * (double)4.0F);
        return this.filledBandlands[(y + i + this.filledBandlands.length) % this.filledBandlands.length];
    }

    public void fillBands(RandomSource random) {
        if (filledBandlands != null) return;

        BlockState[] states = new BlockState[192];
        Arrays.fill(states, base);
        for (Band band : bands) {
            band.fill(states, random);
        }
        filledBandlands = states;
    }
}