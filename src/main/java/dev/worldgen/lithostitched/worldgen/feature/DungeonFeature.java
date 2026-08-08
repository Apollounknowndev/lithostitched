package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.util.valueproviders.IntProviders;
import dev.worldgen.lithostitched.worldgen.feature.config.DungeonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.Optional;
import java.util.function.Predicate;

public class DungeonFeature extends Feature<DungeonConfig> {
    public static final DungeonFeature FEATURE = new DungeonFeature();

    public DungeonFeature() {
        super(DungeonConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<DungeonConfig> context) {
        BlockPos startPos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        DungeonConfig config = context.config();
        Predicate<BlockState> predicate = config.dungeonInvalidBlocks().<Predicate<BlockState>>map(set -> (state -> state.is(set))).orElse(state -> state.is(BlockTags.FEATURES_CANNOT_REPLACE));
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
                    boolean bl = level.getBlockState(currentPos).isSolid();
                    if (y == -1 && !bl) {
                        return false;
                    }

                    if (y == 4 && !bl) {
                        return false;
                    }

                    if ((x == minX || x == maxX || z == minZ || z == maxZ) && y == 0 && level.isEmptyBlock(currentPos) && level.isEmptyBlock(currentPos.above())) {
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
                        BlockState currentState = level.getBlockState(currentPos);
                        if (x != minX && y != -1 && z != minZ && x != maxX && y != 4 && z != maxZ) {
                            if (!currentState.is(Blocks.CHEST) && !currentState.is(Blocks.SPAWNER)) {
                                this.safeSetBlock(level, currentPos, Blocks.CAVE_AIR.defaultBlockState(), predicate);
                            }
                        } else if (currentPos.getY() >= context.chunkGenerator().getMinY() && !level.getBlockState(currentPos.below()).isSolid()) {
                            level.setBlock(currentPos, Blocks.CAVE_AIR.defaultBlockState(), 2);
                        } else if (currentState.isSolid() && !currentState.is(Blocks.CHEST)) {
                            this.safeSetBlock(level, currentPos, y == -1 ?
                                config.floorProvider().getState(level, random, currentPos) :
                                config.wallProvider().getState(level, random, currentPos),
                            predicate);
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
                    if (level.isEmptyBlock(chestPos)) {
                        int solidFaces = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                            if (level.getBlockState(chestPos.relative(direction)).isSolid()) {
                                ++solidFaces;
                            }
                        }

                        if (solidFaces == 1) {
                            this.safeSetBlock(level, chestPos, StructurePiece.reorient(level, chestPos, Blocks.CHEST.defaultBlockState()), predicate);
                            Optional<ChestBlockEntity> chestEntity = level.getBlockEntity(chestPos, BlockEntityTypes.CHEST);
                            chestEntity.ifPresent(chestBlockEntity -> chestBlockEntity.setLootTable(config.lootTable(), random.nextLong()));
                            break;
                        }
                    }
                }
            }

            this.safeSetBlock(level, startPos, Blocks.SPAWNER.defaultBlockState(), predicate);
            BlockEntity blockEntity = level.getBlockEntity(startPos);
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(config.spawnerMobs().getRandom(random).orElse(EntityTypes.PIG), random);
            } else {
                Lithostitched.LOGGER.error("Failed to get spawner block entity for dungeon at block position ({})", startPos);
            }

            return true;
        } else {
            return false;
        }
    }
}