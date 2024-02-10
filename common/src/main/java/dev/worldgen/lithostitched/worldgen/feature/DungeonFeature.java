package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.feature.config.DungeonFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.Optional;
import java.util.function.Predicate;

public class DungeonFeature extends Feature<DungeonFeatureConfig> {

    public DungeonFeature(Codec<DungeonFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DungeonFeatureConfig> context) {
        BlockPos startPos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel world = context.level();
        DungeonFeatureConfig config = context.config();
        Predicate<BlockState> predicate = state -> !state.is(config.dungeonInvalidBlocks());
        int xRadius = config.radius().sample(random);
        int minX = -xRadius - 1;
        int maxX = xRadius + 1;
        int zRadius = config.radius().sample(random);
        int minZ = -zRadius - 1;
        int maxZ = zRadius + 1;
        int openings = 0;

        int x;
        int y;
        int z;
        BlockPos currentPos;
        for(x = minX; x <= maxX; ++x) {
            for(y = -1; y <= 4; ++y) {
                for(z = minZ; z <= maxZ; ++z) {
                    currentPos = startPos.offset(x, y, z);
                    boolean bl = world.getBlockState(currentPos).isSolid();
                    if (y == -1 && !bl) {
                        return false;
                    }

                    if (y == 4 && !bl) {
                        return false;
                    }

                    if ((x == minX || x == maxX || z == minZ || z == maxZ) && y == 0 && world.isEmptyBlock(currentPos) && world.isEmptyBlock(currentPos.above())) {
                        ++openings;
                    }
                }
            }
        }

        if (openings >= config.minOpenings() && openings <= config.maxOpenings()) {
            for(x = minX; x <= maxX; ++x) {
                for(y = 3; y >= -1; --y) {
                    for(z = minZ; z <= maxZ; ++z) {
                        currentPos = startPos.offset(x, y, z);
                        BlockState currentState = world.getBlockState(currentPos);
                        if (x != minX && y != -1 && z != minZ && x != maxX && y != 4 && z != maxZ) {
                            if (!currentState.is(Blocks.CHEST) && !currentState.is(Blocks.SPAWNER)) {
                                this.safeSetBlock(world, currentPos, Blocks.CAVE_AIR.defaultBlockState(), predicate);
                            }
                        } else if (currentPos.getY() >= world.getMinBuildHeight() && !world.getBlockState(currentPos.below()).isSolid()) {
                            world.setBlock(currentPos, Blocks.CAVE_AIR.defaultBlockState(), 2);
                        } else if (currentState.isSolid() && !currentState.is(Blocks.CHEST)) {
                            this.safeSetBlock(world, currentPos, y == -1 ? config.floorProvider().getState(random, currentPos) : config.wallProvider().getState(random, currentPos), predicate);
                        }
                    }
                }
            }

            for(x = 0; x < config.maxChests(); ++x) {
                for(y = 0; y < 3; ++y) {
                    z = startPos.getX() + random.nextInt(xRadius * 2 + 1) - xRadius;
                    int v = startPos.getY();
                    int w = startPos.getZ() + random.nextInt(zRadius * 2 + 1) - zRadius;
                    BlockPos chestPos = new BlockPos(z, v, w);
                    if (world.isEmptyBlock(chestPos)) {
                        int solidFaces = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (world.getBlockState(chestPos.relative(direction)).isSolid()) {
                                ++solidFaces;
                            }
                        }

                        if (solidFaces == 1) {
                            this.safeSetBlock(world, chestPos, StructurePiece.reorient(world, chestPos, Blocks.CHEST.defaultBlockState()), predicate);
                            Optional<ChestBlockEntity> chestEntity = world.getBlockEntity(chestPos, BlockEntityType.CHEST);
                            if (chestEntity.isPresent()) {
                                chestEntity.get().setLootTable(config.lootTable(), random.nextLong());
                            }
                            break;
                        }
                    }
                }
            }

            this.safeSetBlock(world, startPos, Blocks.SPAWNER.defaultBlockState(), predicate);
            BlockEntity blockEntity = world.getBlockEntity(startPos);
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(config.spawnerMobs().getRandomValue(random).orElse(EntityType.PIG), random);
            } else {
                LithostitchedCommon.LOGGER.error(String.format("Failed to get spawner block entity for dungeon at block position (%s)", startPos));
            }

            return true;
        } else {
            return false;
        }
    }
}