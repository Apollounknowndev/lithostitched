package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import dev.worldgen.lithostitched.impl.worldgen.stateprovider.WeightedProvider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Predicate;

public record DungeonFeature(
    int minOpenings, int maxOpenings, IntProvider radius, int maxChests, WeightedList<EntityType<?>> spawnerMobs,
    BlockStateProvider floorProvider, BlockStateProvider wallProvider, 
    Optional<HolderSet<Block>> dungeonInvalidBlocks, ResourceKey<LootTable> lootTable
) implements Feature {
    private static final WeightedList<EntityType<?>> DEFAULT_MOBS = WeightedList.<EntityType<?>>builder()
        .add(EntityTypes.ZOMBIE, 2)
        .add(EntityTypes.SKELETON, 1)
        .add(EntityTypes.SPIDER, 1)
        .build();
    private static final WeightedList<BlockStateProvider> DEFAULT_FLOOR = WeightedList.<BlockStateProvider>builder()
        .add(BlockStateProvider.simple(Blocks.MOSSY_COBBLESTONE), 3)
        .add(BlockStateProvider.simple(Blocks.COBBLESTONE), 1)
        .build();
    
    public static final MapCodec<DungeonFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_openings").orElse(1).forGetter(DungeonFeature::minOpenings),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_openings").orElse(5).forGetter(DungeonFeature::maxOpenings),
        IntProviders.codec(1, 16).fieldOf("radius").orElse(UniformInt.of(2, 3)).forGetter(DungeonFeature::radius),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_chests").orElse(2).forGetter(DungeonFeature::maxChests),
        WeightedList.codec(BuiltInRegistries.ENTITY_TYPE.byNameCodec()).fieldOf("spawner_entity").orElse(DEFAULT_MOBS).forGetter(DungeonFeature::spawnerMobs),
        BlockStateProvider.CODEC.fieldOf("floor_provider").orElse(new WeightedProvider(DEFAULT_FLOOR)).forGetter(DungeonFeature::floorProvider),
        BlockStateProvider.CODEC.fieldOf("wall_provider").orElse(BlockStateProvider.simple(Blocks.COBBLESTONE)).forGetter(DungeonFeature::wallProvider),
        LithostitchedCodecs.BLOCK_SET.optionalFieldOf("dungeon_invalid_blocks").forGetter(DungeonFeature::dungeonInvalidBlocks),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").orElse(BuiltInLootTables.SIMPLE_DUNGEON).forGetter(DungeonFeature::lootTable)
    ).apply(instance, DungeonFeature::new));
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        Predicate<BlockState> predicate = this.dungeonInvalidBlocks()
            .<Predicate<BlockState>>map(set -> (state -> state.is(set)))
            .orElse(state -> state.is(BlockTags.FEATURES_CANNOT_REPLACE))
            .negate();
        int xRadius = this.radius().sample(random);
        int minX = -xRadius - 1;
        int maxX = xRadius + 1;
        int zRadius = this.radius().sample(random);
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
                    currentPos = origin.offset(x, y, z);
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

        if (openings >= this.minOpenings() && openings <= this.maxOpenings()) {
            for(x = minX; x <= maxX; ++x) {
                for(y = 3; y >= -1; --y) {
                    for(z = minZ; z <= maxZ; ++z) {
                        currentPos = origin.offset(x, y, z);
                        BlockState currentState = level.getBlockState(currentPos);
                        if (x != minX && y != -1 && z != minZ && x != maxX && y != 4 && z != maxZ) {
                            if (!currentState.is(Blocks.CHEST) && !currentState.is(Blocks.SPAWNER)) {
                                this.safeSetBlock(level, currentPos, Blocks.CAVE_AIR.defaultBlockState(), predicate);
                            }
                        } else if (currentPos.getY() >= generator.getMinY() && !level.getBlockState(currentPos.below()).isSolid()) {
                            level.setBlock(currentPos, Blocks.CAVE_AIR.defaultBlockState(), 2);
                        } else if (currentState.isSolid() && !currentState.is(Blocks.CHEST)) {
                            this.safeSetBlock(level, currentPos, y == -1 ?
                                this.floorProvider().getState(level, random, currentPos) :
                                this.wallProvider().getState(level, random, currentPos),
                            predicate);
                        }
                    }
                }
            }

            for(x = 0; x < this.maxChests(); ++x) {
                for(y = 0; y < 3; ++y) {
                    z = origin.getX() + random.nextInt(xRadius * 2 + 1) - xRadius;
                    int v = origin.getY();
                    int w = origin.getZ() + random.nextInt(zRadius * 2 + 1) - zRadius;
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
                            chestEntity.ifPresent(chestBlockEntity -> chestBlockEntity.setLootTable(this.lootTable(), random.nextLong()));
                            break;
                        }
                    }
                }
            }

            this.safeSetBlock(level, origin, Blocks.SPAWNER.defaultBlockState(), predicate);
            BlockEntity blockEntity = level.getBlockEntity(origin);
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(this.spawnerMobs().getRandom(random).orElse(EntityTypes.PIG), random);
            } else {
                Lithostitched.LOGGER.error("Failed to get spawner block entity for dungeon at block position ({})", origin);
            }

            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}