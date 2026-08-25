package dev.worldgen.lithostitched.impl.worldgen.tree.rootplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class LargeMangroveRootPlacer extends MangroveRootPlacer {

    List<Pair<Vec3i, Direction>> rootLocations = List.of(
            Pair.of(new Vec3i(2, 0, 0), Direction.EAST),
            Pair.of(new Vec3i(2, 0, 1), Direction.EAST),
            Pair.of(new Vec3i(0, 0, 2), Direction.SOUTH),
            Pair.of(new Vec3i(1, 0, 2), Direction.SOUTH),
            Pair.of(new Vec3i(-1, 0, 0), Direction.WEST),
            Pair.of(new Vec3i(-1, 0, 1), Direction.WEST),
            Pair.of(new Vec3i(0, 0, -1), Direction.NORTH),
            Pair.of(new Vec3i(1, 0, -1), Direction.NORTH)
    );

    public static final MapCodec<LargeMangroveRootPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> rootPlacerParts(instance)
                    .and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter(placer -> placer.mangroveRootPlacement))
                    .apply(instance, LargeMangroveRootPlacer::new)
    );
    public static final RootPlacerType<LargeMangroveRootPlacer> TYPE = new RootPlacerType<>(CODEC);
    public final MangroveRootPlacement mangroveRootPlacement;

    public LargeMangroveRootPlacer(IntProvider trunkOffset,
                          BlockStateProvider rootProvider,
                          Optional<AboveRootPlacement> aboveRootPlacement,
                          MangroveRootPlacement largePlacement) {
        super(trunkOffset, rootProvider, aboveRootPlacement, largePlacement);
        this.mangroveRootPlacement = largePlacement;
    }

    @Override
    protected RootPlacerType<?> type() {
        return TYPE;
    }

    @Override
    public boolean placeRoots(
            WorldGenLevel level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            BlockPos pos,
            BlockPos trunkOrigin,
            TreeConfiguration treeConfig
    ) {
        List<BlockPos> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pos.mutable();

        while (mutable.getY() < trunkOrigin.getY()) {
            if (!this.canPlaceRoot(level, mutable))
                return false;
            mutable.move(Direction.UP);
        }

        list.add(trunkOrigin.below());

        for (Pair<Vec3i, Direction> pair : rootLocations) {
            int offset = trunkOffsetY.sample(random);
            if(offset > 0) {
                BlockPos blockpos = trunkOrigin.offset(pair.getKey()).above(offset-1);
                List<BlockPos> simulate = Lists.newArrayList();
                if (!this.simulateRoots(level, random, blockpos, pair.getValue(), trunkOrigin, simulate, 0)) {
                    continue;
                }

                list.addAll(simulate);
                list.add(blockpos);
            }
        }

        for (BlockPos offset : list) {
            this.placeRoot(level, blockSetter, random, offset, treeConfig);
        }

        return true;
    }

    public BlockPos getTrunkOrigin(BlockPos origin, RandomSource random) {
        return origin;
    }
}
