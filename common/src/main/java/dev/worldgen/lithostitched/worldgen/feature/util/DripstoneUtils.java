package dev.worldgen.lithostitched.worldgen.feature.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Consumer;

public class DripstoneUtils {
    public static double getDripstoneHeight(double $$0, double $$1, double $$2, double $$3) {
        if ($$0 < $$3) {
            $$0 = $$3;
        }

        double $$4 = 0.384;
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
            float $$3 = 6.0F;
            float $$4 = 6.0F / (float)$$2;

            for (float $$5 = 0.0F; $$5 < (float) (Math.PI * 2); $$5 += $$4) {
                int $$6 = (int)(Mth.cos($$5) * (float)$$2);
                int $$7 = (int)(Mth.sin($$5) * (float)$$2);
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

    protected static void buildBaseToTipColumn(Direction $$0, int $$1, boolean $$2, Consumer<BlockState> $$3) {
        if ($$1 >= 3) {
            $$3.accept(createPointedDripstone($$0, DripstoneThickness.BASE));

            for (int $$4 = 0; $$4 < $$1 - 3; $$4++) {
                $$3.accept(createPointedDripstone($$0, DripstoneThickness.MIDDLE));
            }
        }

        if ($$1 >= 2) {
            $$3.accept(createPointedDripstone($$0, DripstoneThickness.FRUSTUM));
        }

        if ($$1 >= 1) {
            $$3.accept(createPointedDripstone($$0, $$2 ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }
    }

    protected static void growPointedDripstone(LevelAccessor level, HolderSet<Block> replaceable, BlockPos pos, Direction direction, int $$3, boolean $$4) {
        if (isReplaceable(level.getBlockState(pos.relative(direction.getOpposite())), replaceable)) {
            BlockPos.MutableBlockPos $$5 = pos.mutable();
            buildBaseToTipColumn(direction, $$3, $$4, $$3x -> {
                if ($$3x.is(Blocks.POINTED_DRIPSTONE)) {
                    $$3x = $$3x.setValue(PointedDripstoneBlock.WATERLOGGED, Boolean.valueOf(level.isWaterAt($$5)));
                }

                level.setBlock($$5, $$3x, 2);
                $$5.move(direction);
            });
        }
    }

    protected static boolean placeDripstoneBlockIfPossible(LevelAccessor level, BlockStateProvider stateProvider, RandomSource random, HolderSet<Block> replaceable, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(replaceable)) {
            level.setBlock(pos, stateProvider.getState(random, pos), 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState createPointedDripstone(Direction direction, DripstoneThickness thickness) {
        return Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction).setValue(PointedDripstoneBlock.THICKNESS, thickness);
    }

    public static boolean isReplaceableOrLava(BlockState state, HolderSet<Block> replaceable) {
        return isReplaceable(state, replaceable) || state.is(Blocks.LAVA);
    }

    public static boolean isReplaceable(BlockState state, HolderSet<Block> replaceable) {
        return state.is(replaceable);
    }

    public static boolean isEmptyOrWater(BlockState $$0x) {
        return $$0x.isAir() || $$0x.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState $$0) {
        return !$$0.isAir() && !$$0.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState $$0x) {
        return $$0x.isAir() || $$0x.is(Blocks.WATER) || $$0x.is(Blocks.LAVA);
    }
}
