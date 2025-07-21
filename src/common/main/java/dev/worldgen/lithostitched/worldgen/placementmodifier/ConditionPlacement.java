package dev.worldgen.lithostitched.worldgen.placementmodifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConditionPlacement extends PlacementFilter {
    public static final MapCodec<ConditionPlacement> CODEC = PlacementCondition.CODEC.fieldOf("condition").xmap(ConditionPlacement::new, ConditionPlacement::condition);
    public static final PlacementModifierType<ConditionPlacement> TYPE = () -> CODEC;


    private final PlacementCondition condition;

    public ConditionPlacement(PlacementCondition condition) {
        this.condition = condition;
    }

    public PlacementCondition condition() {
        return this.condition;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource randomSource, BlockPos blockPos) {
        return this.condition.test(context, blockPos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE;
    }
}
