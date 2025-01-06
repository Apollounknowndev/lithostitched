package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

public record NotPlacementCondition(PlacementCondition condition) implements PlacementCondition {
    public static final MapCodec<NotPlacementCondition> CODEC = PlacementCondition.BASE_CODEC.fieldOf("condition").xmap(NotPlacementCondition::new, NotPlacementCondition::condition);

    @Override
    public boolean test(Context context, BlockPos pos) {
        return !this.condition.test(context, pos);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
