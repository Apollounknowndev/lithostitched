package dev.worldgen.lithostitched.worldgen.feature.util;

import dev.worldgen.lithostitched.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DripstoneUtils {
    public static double getDripstoneHeight(double $$0, double $$1, double $$2, double $$3) {
        if ($$0 < $$3) {
            $$0 = $$3;
        }

        double $$5 = $$0 / $$1 * 0.384;
        double $$6 = 0.75 * Math.pow($$5, 1.3333333333333333);
        double $$7 = Math.pow($$5, 0.6666666666666666);
        double $$8 = 0.3333333333333333 * Math.log($$5);
        double $$9 = $$2 * ($$6 - $$7 - $$8);
        $$9 = Math.max($$9, 0.0);
        return $$9 / 0.384 * $$1;
    }

    public static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel $$0, BlockPos $$1, int $$2) {
        if (isEmptyOrWaterOrLava($$0, $$1)) {
            return false;
        } else {
            float $$4 = 6.0F / (float)$$2;

            for (float $$5 = 0.0F; $$5 < (float) (Math.PI * 2); $$5 += $$4) {
                int $$6 = (int)(MiscUtils.cos($$5) * (float)$$2);
                int $$7 = (int)(MiscUtils.sin($$5) * (float)$$2);
                if (isEmptyOrWaterOrLava($$0, $$1.offset($$6, 0, $$7))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmptyOrWater(LevelAccessor $$0, BlockPos $$1) {
        return $$0.isStateAtPosition($$1, DripstoneUtils::isEmptyOrWater);
    }

    public static boolean isEmptyOrWaterOrLava(LevelAccessor $$0, BlockPos $$1) {
        return $$0.isStateAtPosition($$1, DripstoneUtils::isEmptyOrWaterOrLava);
    }

    public static boolean isReplaceableOrLava(BlockState state, HolderSet<Block> replaceable) {
        return isReplaceable(state, replaceable) || state.is(Blocks.LAVA);
    }

    public static boolean isReplaceable(BlockState state, HolderSet<Block> replaceable) {
        return state.is(replaceable);
    }

    public static boolean isEmptyOrWater(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
    }
}
