package dev.worldgen.lithostitched.worldgen.placementmodifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;

public record ConditionPlacement(PlacementCondition condition) implements PlacementFilter {
    public static final MapCodec<ConditionPlacement> CODEC = PlacementCondition.CODEC.fieldOf("condition").xmap(ConditionPlacement::new, ConditionPlacement::condition);

    @Override
    public boolean shouldPlace(PlacementContext context, RandomSource randomSource, BlockPos blockPos) {
        return this.condition.test(context, blockPos);
    }
    
    @Override
    public MapCodec<? extends PlacementFilter> codec() {
        return CODEC;
    }
}
