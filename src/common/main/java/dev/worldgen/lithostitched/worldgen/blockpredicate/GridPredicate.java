
package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.placementcondition.GridPlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record GridPredicate(int radius, int distBetweenPoints, BlockPredicate predicate, InclusiveRange<Integer> allowedCount) implements BlockPredicate {
    public static final MapCodec<GridPredicate> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        ExtraCodecs.POSITIVE_INT.fieldOf("radius").forGetter(GridPredicate::radius),
        ExtraCodecs.POSITIVE_INT.fieldOf("distance_between_points").forGetter(GridPredicate::distBetweenPoints),
        BlockPredicate.CODEC.fieldOf("predicate").forGetter(GridPredicate::predicate),
        LithostitchedCodecs.INT_RANGE.fieldOf("allowed_count").forGetter(GridPredicate::allowedCount)
    ).apply(i, GridPredicate::new));
    public static final BlockPredicateType<GridPredicate> TYPE = () -> CODEC;

    @Override
    public boolean test(WorldGenLevel level, BlockPos origin) {
        int count = 0;
        
        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int x = origin.getX() - this.radius; x <= origin.getX() + this.radius; x += this.distBetweenPoints) {
            for (int z = origin.getZ() - this.radius; z <= origin.getZ() + this.radius; z += this.distBetweenPoints) {
                if (this.predicate.test(level, pos.setX(x).setZ(z).immutable())) {
                    count++;
                    if (this.allowedCount.maxInclusive() < count) return false;
                }
            }
        }
        
        return this.allowedCount.isValueInRange(count);
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
