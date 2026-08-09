package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public record WellFeature(BlockStateProvider groundProvider, BlockStateProvider suspiciousProvider, BlockStateProvider standardProvider, BlockStateProvider slabProvider, BlockStateProvider fluidProvider, IntProvider suspiciousPlacements, ResourceKey<LootTable> suspiciousLootTable) implements Feature {
    public static final MapCodec<WellFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("ground_provider").orElse(BlockStateProvider.simple(Blocks.SAND)).forGetter(WellFeature::groundProvider),
        BlockStateProvider.CODEC.fieldOf("suspicious_provider").orElse(BlockStateProvider.simple(Blocks.SUSPICIOUS_SAND)).forGetter(WellFeature::suspiciousProvider),
        BlockStateProvider.CODEC.fieldOf("standard_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE)).forGetter(WellFeature::standardProvider),
        BlockStateProvider.CODEC.fieldOf("slab_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE_SLAB)).forGetter(WellFeature::slabProvider),
        BlockStateProvider.CODEC.fieldOf("fluid_provider").orElse(BlockStateProvider.simple(Blocks.WATER)).forGetter(WellFeature::fluidProvider),
        IntProviders.codec(0, 4).fieldOf("suspicious_block_placements").orElse(ConstantInt.of(1)).forGetter(WellFeature::suspiciousPlacements),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("suspicious_loot_table").orElse(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY).forGetter(WellFeature::suspiciousLootTable)
    ).apply(instance, WellFeature::new));
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos pos;
        int x;
        int y;
        int z;
        for(x = -2; x <= 2; ++x) {
            for(z = -2; z <= 2; ++z) {
                if (level.isEmptyBlock(origin.offset(x, -1, z)) && level.isEmptyBlock(origin.offset(x, -2, z))) {
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
                        blockProvider = this.standardProvider();
                    } else if (y < 0) {
                        if (axisAligned && !outer) {
                            blockProvider = y == -2 ? this.groundProvider() : this.fluidProvider();
                        } else {
                            blockProvider = this.standardProvider();
                        }
                    } else if (outer) {
                        blockProvider = y > 0 ? BlockStateProvider.simple(Blocks.AIR) : axisAligned ? this.slabProvider() : this.standardProvider();
                    } else if (middle && y != 3) {
                        blockProvider = this.standardProvider();
                    } else if (y == 3) {
                        blockProvider = inner ? this.standardProvider() : this.slabProvider();
                    } else {
                        blockProvider = BlockStateProvider.simple(Blocks.AIR);
                    }
                    level.setBlock(pos, blockProvider.getState(level, random, pos), 2);
                }
            }
        }
        for (int i = 0; i < this.suspiciousPlacements().sample(random); i++) {
            for (int offset = 0; offset < 2; offset++) {
                pos = origin.below(offset+2).relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                level.setBlock(pos, this.suspiciousProvider().getState(level, random, pos), 2);
                Optional<BrushableBlockEntity> susBlock = level.getBlockEntity(pos, BlockEntityTypes.BRUSHABLE_BLOCK);
                if (susBlock.isPresent()) {
                    susBlock.get().setLootTable(this.suspiciousLootTable(), pos.asLong());
                }
            }
        }
        return true;
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}