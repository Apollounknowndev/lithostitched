package dev.worldgen.lithostitched.impl.worldgen.tree.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;

public class BranchedMegaJungleFoliagePlacer extends MegaJungleFoliagePlacer {
    public static final MapCodec<BranchedMegaJungleFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(i -> foliagePlacerParts(i).and(
        Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height)
    ).apply(i, BranchedMegaJungleFoliagePlacer::new));
    public static final FoliagePlacerType<BranchedMegaJungleFoliagePlacer> TYPE = new FoliagePlacerType<>(CODEC);

    public BranchedMegaJungleFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return TYPE;
    }

    @Override
    protected void createFoliage(
            WorldGenLevel level,
            FoliageSetter blockSetter,
            RandomSource random,
            TreeConfiguration config,
            int maxFreeTreeHeight,
            FoliageAttachment attachment,
            int foliageHeight,
            int foliageRadius,
            int offset
    ) {
        int i = attachment.doubleTrunk() ? foliageHeight : 1 + random.nextInt(2);

        // Place branches. This is the only modified part of this compared to MegaPileFoliagePlacer
        BlockPos blockpos = attachment.pos();
        if(foliageRadius+3 > 2 && attachment.doubleTrunk()) {
            int heightOffset = 0;
            for (int length = 0; length < foliageRadius+1; length++) {
                // Move branches into leaves on last block
                if(length==(foliageRadius)) heightOffset = 1;
                // Place branches
                BlockPos pos = blockpos.offset(-1 - length, (offset-i)-1+heightOffset, random.nextInt(2));
                blockSetter.set(pos, config.trunkProvider.getState(level, random, pos).trySetValue(BlockStateProperties.AXIS, Direction.Axis.X));
                pos = blockpos.offset(random.nextInt(2), (offset-i) - 1+heightOffset, -1 - length);
                blockSetter.set(pos, config.trunkProvider.getState(level, random, pos).trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z));
                pos = blockpos.offset(2 + length, (offset-i) - 1+heightOffset, random.nextInt(2));
                blockSetter.set(pos, config.trunkProvider.getState(level, random, pos).trySetValue(BlockStateProperties.AXIS, Direction.Axis.X));
                pos = blockpos.offset(random.nextInt(2), (offset-i) - 1+heightOffset, 2 + length);
                blockSetter.set(pos, config.trunkProvider.getState(level, random, pos).trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z));
            }
        }

        int modFoliageRadius = attachment.doubleTrunk() ? foliageRadius : Math.min(foliageRadius, 2);
        for (int j = offset; j >= offset - i; j--) {
            int k = modFoliageRadius + attachment.radiusOffset() + 1 - j;
            this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), k, j, attachment.doubleTrunk());
        }
    }

    @Override
    protected boolean shouldSkipLocation( RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return localX * localX + localZ * localZ > range * range;
    }
}

