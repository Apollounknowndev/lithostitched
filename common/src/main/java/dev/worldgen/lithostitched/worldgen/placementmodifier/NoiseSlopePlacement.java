package dev.worldgen.lithostitched.worldgen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NoiseSlopePlacement extends PlacementModifier {
    public static final MapCodec<NoiseSlopePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseSlopePlacement::noise),
        Codec.INT.fieldOf("slope").forGetter(NoiseSlopePlacement::slope),
        Codec.INT.fieldOf("offset").orElse(0).forGetter(NoiseSlopePlacement::offset),
        Codec.DOUBLE.fieldOf("xz_scale").forGetter(NoiseSlopePlacement::xzScale),
        Codec.DOUBLE.fieldOf("y_scale").forGetter(NoiseSlopePlacement::yScale)
    ).apply(instance, NoiseSlopePlacement::new));
    public static final PlacementModifierType<NoiseSlopePlacement> TYPE = () -> CODEC;

    private final ResourceKey<NormalNoise.NoiseParameters> noise;
    private final int slope;
    private final int offset;
    private final double xzScale;
    private final double yScale;

    public NoiseSlopePlacement(ResourceKey<NormalNoise.NoiseParameters> noise, int slope, int offset, double xzScale, double yScale) {
        this.noise = noise;
        this.slope = slope;
        this.offset = offset;
        this.xzScale = xzScale;
        this.yScale = yScale;
    }

    public ResourceKey<NormalNoise.NoiseParameters> noise() {
        return this.noise;
    }

    public int slope() {
        return this.slope;
    }

    public int offset() {
        return this.offset;
    }

    public double xzScale() {
        return this.xzScale;
    }

    public double yScale() {
        return this.yScale;
    }


    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return IntStream.range(0, this.count(context, pos)).mapToObj(__ -> pos);
    }

    protected int count(PlacementContext context, BlockPos pos) {
        RandomState state = context.getLevel().getLevel().getChunkSource().randomState();
        double value = state.getOrCreateNoise(this.noise).getValue(pos.getX() * this.xzScale, pos.getY() * this.yScale, pos.getZ() * this.xzScale);
        return (int) Math.ceil(value * this.slope) + this.offset;
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE;
    }
}
