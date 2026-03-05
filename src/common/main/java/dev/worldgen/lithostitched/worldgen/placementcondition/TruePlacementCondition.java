package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;

public class TruePlacementCondition implements PlacementCondition {
    public static final TruePlacementCondition INSTANCE = new TruePlacementCondition();
    public static final MapCodec<TruePlacementCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public boolean test(Context context, BlockPos pos) {
        return true;
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
