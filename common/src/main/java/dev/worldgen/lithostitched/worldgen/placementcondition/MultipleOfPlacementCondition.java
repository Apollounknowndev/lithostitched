package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public record MultipleOfPlacementCondition(List<PlacementCondition> conditions, InclusiveRange<Integer> allowedCount) implements PlacementCondition {
    public static final MapCodec<MultipleOfPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PlacementCondition.BASE_CODEC.listOf().fieldOf("conditions").forGetter(MultipleOfPlacementCondition::conditions),
        InclusiveRange.INT.fieldOf("allowed_count").forGetter(MultipleOfPlacementCondition::allowedCount)
    ).apply(instance, MultipleOfPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        int count = 0;
        for (PlacementCondition condition : this.conditions) {
            if (condition.test(context, pos)) {
                count++;
                if (this.allowedCount.maxInclusive() < count) return false;
            }
        }
        return this.allowedCount.isValueInRange(count);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
