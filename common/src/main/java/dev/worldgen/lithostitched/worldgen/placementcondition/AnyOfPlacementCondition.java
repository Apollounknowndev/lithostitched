package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public record AnyOfPlacementCondition(List<PlacementCondition> conditions) implements PlacementCondition {
    public static final MapCodec<AnyOfPlacementCondition> CODEC = PlacementCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AnyOfPlacementCondition::new, AnyOfPlacementCondition::conditions);

    @Override
    public boolean test(Context context, BlockPos pos) {
        for (PlacementCondition condition : this.conditions) {
            if (condition.test(context, pos)) return true;
        }
        return false;
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
