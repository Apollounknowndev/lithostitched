package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

public record OffsetPlacementCondition(PlacementCondition condition, BlockPos offset) implements PlacementCondition {
    public static final MapCodec<OffsetPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PlacementCondition.BASE_CODEC.fieldOf("condition").forGetter(OffsetPlacementCondition::condition),
        BlockPos.CODEC.fieldOf("offset").forGetter(OffsetPlacementCondition::offset)
    ).apply(instance, OffsetPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        return this.condition.test(context, pos.offset(this.offset));
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
