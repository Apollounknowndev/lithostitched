package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;

public record GridPlacementCondition(int radius, int distBetweenPoints, PlacementCondition condition, InclusiveRange<Integer> allowedCount) implements PlacementCondition {
    public static final MapCodec<GridPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.POSITIVE_INT.fieldOf("radius").forGetter(GridPlacementCondition::radius),
        Codec.intRange(4, 1024).fieldOf("distance_between_points").forGetter(GridPlacementCondition::distBetweenPoints),
        PlacementCondition.BASE_CODEC.fieldOf("condition").forGetter(GridPlacementCondition::condition),
        InclusiveRange.INT.fieldOf("allowed_count").forGetter(GridPlacementCondition::allowedCount)
    ).apply(instance, GridPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        int count = 0;

        for (int x = pos.getX() - this.radius; x <= pos.getX() + this.radius; x += this.distBetweenPoints) {
            for (int z = pos.getZ() - this.radius; z <= pos.getZ() + this.radius; z += this.distBetweenPoints) {
                if (this.condition.test(context, new BlockPos(x, pos.getY(), z))) {
                    count++;
                    if (this.allowedCount.maxInclusive() < count) return false;
                }
            }
        }

        return this.allowedCount.isValueInRange(count);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
