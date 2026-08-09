package dev.worldgen.lithostitched.impl.worldgen.placementmodifier;

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
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.function.Consumer;

public record NoiseSlopePlacement(ResourceKey<NormalNoise> noise, int slope, int offset, double xzScale, double yScale) implements PlacementModifier {
    public static final MapCodec<NoiseSlopePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseSlopePlacement::noise),
        Codec.INT.fieldOf("slope").forGetter(NoiseSlopePlacement::slope),
        Codec.INT.fieldOf("offset").orElse(0).forGetter(NoiseSlopePlacement::offset),
        Codec.DOUBLE.fieldOf("xz_scale").forGetter(NoiseSlopePlacement::xzScale),
        Codec.DOUBLE.fieldOf("y_scale").forGetter(NoiseSlopePlacement::yScale)
    ).apply(instance, NoiseSlopePlacement::new));
    
    @Override
    public void modify(PlacementContext context, RandomSource random, BlockPos origin, Consumer<BlockPos> output) {
        int count = this.count(context, origin);
        for (int i = 0; i < count; i++) {
            output.accept(origin);
        }
    }

    private int count(PlacementContext context, BlockPos pos) {
        RandomState state = context.getLevel().getLevel().getChunkSource().randomState();
        double value = state.getOrCreateNoise(this.noise).get(pos.getX() * this.xzScale, pos.getY() * this.yScale, pos.getZ() * this.xzScale);
        return (int) Math.ceil(value * this.slope) + this.offset;
    }
    
    @Override
    public MapCodec<? extends PlacementModifier> codec() {
        return CODEC;
    }
}
