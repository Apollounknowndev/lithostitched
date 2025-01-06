package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public record AllOfPlacementCondition(List<PlacementCondition> conditions) implements PlacementCondition {
    public static final MapCodec<AllOfPlacementCondition> CODEC = PlacementCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AllOfPlacementCondition::new, AllOfPlacementCondition::conditions);

    public AllOfPlacementCondition(List<PlacementCondition> conditions) {
        this.conditions = new ArrayList<>(conditions);
    }

    public void appendCondition(PlacementCondition condition) {
        this.conditions.add(condition);
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        for (PlacementCondition condition : this.conditions) {
            if (!condition.test(context, pos)) return false;
        }
        return true;
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
