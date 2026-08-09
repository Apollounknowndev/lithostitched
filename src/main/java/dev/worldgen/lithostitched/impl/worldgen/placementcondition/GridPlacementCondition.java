package dev.worldgen.lithostitched.impl.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;

public record GridPlacementCondition(int radius, int distBetweenPoints, PlacementCondition condition, InclusiveRange<Integer> allowedCount) implements PlacementCondition {
    public static final MapCodec<GridPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.POSITIVE_INT.fieldOf("radius").forGetter(GridPlacementCondition::radius),
        ExtraCodecs.POSITIVE_INT.fieldOf("distance_between_points").forGetter(GridPlacementCondition::distBetweenPoints),
        PlacementCondition.BASE_CODEC.fieldOf("condition").forGetter(GridPlacementCondition::condition),
        LithostitchedCodecs.INT_RANGE.fieldOf("allowed_count").forGetter(GridPlacementCondition::allowedCount)
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
