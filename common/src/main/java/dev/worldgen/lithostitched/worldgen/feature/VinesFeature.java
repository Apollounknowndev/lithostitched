package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.VinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class VinesFeature extends Feature<VinesConfig> {
    public static final VinesFeature FEATURE = new VinesFeature();
    public VinesFeature() {
        super(VinesConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<VinesConfig> context) {
        VinesConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos pos = context.origin().mutable();

        var states = config.blocks().getRandomValue(context.random());

        if (states.isEmpty()) return false;

        boolean anyPlaced = false;

        for (int i = 0; i < config.maxLength().sample(context.random()); i++) {
            if (!level.isEmptyBlock(pos)) break;

            Block vine = states.get();

            boolean placed = false;

            for (Direction direction : Direction.values()) {
                if (direction == Direction.DOWN) continue;

                if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction) && config.canPlaceOn(level.getBlockState(pos.relative(direction)))) {
                    level.setBlock(pos, vine.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), true), 2);
                    placed = true;
                }

                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.getBlock() instanceof VineBlock) {
                    if (
                        aboveState.getValue(VineBlock.NORTH) ||
                        aboveState.getValue(VineBlock.EAST) ||
                        aboveState.getValue(VineBlock.SOUTH) ||
                        aboveState.getValue(VineBlock.WEST)
                    ) {
                        level.setBlock(pos, vine.withPropertiesOf(aboveState).setValue(VineBlock.UP, false), 2);
                        placed = true;

                    }
                }
            }

            if (!placed) break;

            anyPlaced = true;
            pos.move(Direction.DOWN);
        }

        return anyPlaced;
    }
}
