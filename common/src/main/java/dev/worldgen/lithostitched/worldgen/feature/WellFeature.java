package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.worldgen.feature.config.WellFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public class WellFeature extends Feature<WellFeatureConfig> {

    public WellFeature(Codec<WellFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WellFeatureConfig> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        WellFeatureConfig config = context.config();
        RandomSource random = context.random();

        BlockPos pos;
        int x;
        int y;
        int z;
        for(x = -2; x <= 2; ++x) {
            for(z = -2; z <= 2; ++z) {
                if (world.isEmptyBlock(origin.offset(x, -1, z)) && world.isEmptyBlock(origin.offset(x, -2, z))) {
                    return false;
                }
            }
        }

        for(x = -2; x <= 2; ++x) {
            for(y = -3; y <= 3; ++y) {
                for(z = -2; z <= 2; ++z) {
                    pos = origin.offset(x, y, z);
                    boolean outer = Math.abs(x) == 2 || Math.abs(z) == 2;
                    boolean middle = Math.abs(x) == 1 && Math.abs(z) == 1;
                    boolean inner = x == 0 && z == 0;
                    boolean axisAligned = x == 0 || z == 0;
                    BlockStateProvider blockProvider;
                    if (y == -3) {
                        blockProvider = config.standardProvider();
                    } else if (y < 0) {
                        if (axisAligned && !outer) {
                            blockProvider = y == -2 ? config.groundProvider() : config.fluidProvider();
                        } else {
                            blockProvider = config.standardProvider();
                        }
                    } else if (outer) {
                        blockProvider = y > 0 ? BlockStateProvider.simple(Blocks.AIR) : axisAligned ? config.slabProvider() : config.standardProvider();
                    } else if (middle && y != 3) {
                        blockProvider = config.standardProvider();
                    } else if (y == 3) {
                        blockProvider = inner ? config.standardProvider() : config.slabProvider();
                    } else {
                        blockProvider = BlockStateProvider.simple(Blocks.AIR);
                    }
                    world.setBlock(pos, blockProvider.getState(random, pos), 2);
                }
            }
        }
        for (int i = 0; i < config.suspiciousPlacements().sample(random); i++) {
            for (int offset = 0; offset < 2; offset++) {
                pos = origin.below(offset+2).relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                world.setBlock(pos, config.suspiciousProvider().getState(random, pos), 2);
                Optional<BrushableBlockEntity> susBlock = world.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK);
                if (susBlock.isPresent()) {
                    susBlock.get().setLootTable(config.suspiciousLootTable(), pos.asLong());
                }
            }
        }
        return true;
    }
}