package dev.worldgen.lithostitched.worldgen.placementmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

import static net.minecraft.util.valueproviders.ConstantInt.ZERO;

public class OffsetPlacement extends PlacementModifier {
    public static final MapCodec<OffsetPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.codec(-16, 16).orElse(ZERO).fieldOf("x_offset").forGetter(OffsetPlacement::xOffset),
        IntProvider.CODEC.orElse(ZERO).fieldOf("y_offset").forGetter(OffsetPlacement::yOffset),
        IntProvider.codec(-16, 16).orElse(ZERO).fieldOf("z_offset").forGetter(OffsetPlacement::zOffset)
    ).apply(instance, OffsetPlacement::new));
    public static final PlacementModifierType<OffsetPlacement> TYPE = () -> CODEC;

    private final IntProvider xOffset;
    private final IntProvider yOffset;
    private final IntProvider zOffset;

    public OffsetPlacement(IntProvider xOffset, IntProvider yOffset, IntProvider zOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public IntProvider xOffset() {
        return this.xOffset;
    }

    public IntProvider yOffset() {
        return this.yOffset;
    }

    public IntProvider zOffset() {
        return this.zOffset;
    }

    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int x = pos.getX() + this.xOffset.sample(random);
        int y = pos.getY() + this.yOffset.sample(random);
        int z = pos.getZ() + this.zOffset.sample(random);
        return Stream.of(new BlockPos(x, y, z));
    }

    public PlacementModifierType<?> type() {
        return TYPE;
    }
}
